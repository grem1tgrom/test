package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private User ensureUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = ensureUser(ownerId);
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название вещи не должно быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не должно быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Статус доступности обязателен");
        }
        Item item = ItemMapper.toModel(dto, owner);
        Item saved = itemRepository.save(item);
        log.info("Создана вещь id={} владельцем id={}", saved.getId(), ownerId);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patchDto) {
        User owner = ensureUser(ownerId);
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));
        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Вещь не найдена: id=" + itemId);
        }
        ItemMapper.merge(existing, patchDto);
        Item saved = itemRepository.save(existing);
        log.info("Обновлена вещь id={} владельцем id={}", itemId, ownerId);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDetailsDto get(Long requesterId, Long itemId) {
        User requester = ensureUser(requesterId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        ItemDetailsDto dto = ItemMapper.toDetailsDto(item);

        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        dto.setComments(comments);

        if (item.getOwner().getId().equals(requester.getId())) {
            enrichWithBookings(dto, item);
        }
        return dto;
    }

    @Override
    public List<ItemDetailsDto> getOwnerItems(Long ownerId) {
        User owner = ensureUser(ownerId);
        List<Item> items = itemRepository.findByOwner(owner);
        return items.stream().map(item -> {
            ItemDetailsDto dto = ItemMapper.toDetailsDto(item);
            List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
            dto.setComments(comments);
            enrichWithBookings(dto, item);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = ensureUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        LocalDateTime now = LocalDateTime.now();
        boolean canComment = bookingRepository.existsFinishedBookingForUser(itemId, userId, now);
        if (!canComment) {
            throw new ValidationException("Оставить отзыв можно только после завершённой аренды");
        }
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не должен быть пустым");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment saved = commentRepository.save(comment);
        log.info("Добавлен комментарий id={} к вещи id={} пользователем id={}", saved.getId(), itemId, userId);
        return CommentMapper.toDto(saved);
    }

    private void enrichWithBookings(ItemDetailsDto dto, Item item) {
        List<Booking> bookings = bookingRepository.findByItem(item);
        if (bookings.isEmpty()) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return;
        }
        LocalDateTime now = LocalDateTime.now();

        bookings.stream()
                .filter(b -> !b.getEnd().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd))
                .ifPresent(b -> dto.setLastBooking(BookingMapper.toShortDto(b)));

        bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(b -> dto.setNextBooking(BookingMapper.toShortDto(b)));
    }
}