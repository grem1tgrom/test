package ru.practicum.shareit. booking.service;

import lombok. RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain. Sort;
import org.springframework. stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto. BookingState;
import ru.practicum. shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit. booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru. practicum.shareit.exception. ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit. user.model.User;
import ru.practicum.shareit. user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream. Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "start");

    private User ensureUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("Недопустимый пользователь: id=" + userId));
    }

    @Override
    public BookingDto create(Long userId, BookingRequestDto request) {
        User booker = ensureUser(userId);
        Item item = itemRepository. findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + request.getItemId()));

        if (! Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        LocalDateTime now = LocalDateTime.now();
        if (request.getStart() == null || request.getEnd() == null ||
                ! request.getEnd().isAfter(request.getStart()) || !request.getStart().isAfter(now)) {
            throw new ValidationException("Некорректный период бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        log.info("Создано бронирование id={} пользователем id={} для вещи id={}", saved. getId(), userId, item.getId());
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        User owner = ensureUser(ownerId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + bookingId));
        
        if (!booking.getItem().getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Бронирование не найдено: id=" + bookingId);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        log.info("{} бронирование id={} владельцем id={}",
                approved ? "Подтверждено" : "Отклонено", saved.getId(), ownerId);
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        User requester = ensureUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: id=" + bookingId));

        Long ownerId = booking.getItem().getOwner(). getId();
        Long bookerId = booking.getBooker().getId();
        if (! requester.getId().equals(ownerId) && !requester. getId().equals(bookerId)) {
            throw new NotFoundException("Бронирование не найдено: id=" + bookingId);
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingState state) {
        ensureUser(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state == null ? BookingState.ALL : state) {
            case ALL -> bookingRepository.findByBookerId(userId, SORT_DESC);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, SORT_DESC);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now, SORT_DESC);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now, SORT_DESC);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_DESC);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, SORT_DESC);
        };
        return bookings.stream().map(BookingMapper::toDto). collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, BookingState state) {
        ensureUser(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state == null ? BookingState.ALL : state) {
            case ALL -> bookingRepository. findByOwnerId(ownerId, SORT_DESC);
            case CURRENT -> bookingRepository.findOwnerCurrent(ownerId, now, now, SORT_DESC);
            case PAST -> bookingRepository.findOwnerPast(ownerId, now, SORT_DESC);
            case FUTURE -> bookingRepository.findOwnerFuture(ownerId, now, SORT_DESC);
            case WAITING -> bookingRepository.findOwnerByStatus(ownerId, BookingStatus.WAITING, SORT_DESC);
            case REJECTED -> bookingRepository.findOwnerByStatus(ownerId, BookingStatus.REJECTED, SORT_DESC);
        };
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }
}