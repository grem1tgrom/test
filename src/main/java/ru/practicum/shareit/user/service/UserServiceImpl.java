package ru. practicum.shareit.user. service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org. springframework.stereotype.Service;
import org.springframework.transaction.annotation. Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit. exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit. user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream. Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже используется");
        }
        User user = UserMapper.toModel(dto);
        User saved = userRepository.save(user);
        log.info("Создан пользователь id={}", saved.getId());
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository. findById(id)
                . orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll(). stream(). map(UserMapper::toDto). collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto patchDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));
        if (patchDto. getEmail() != null && !patchDto.getEmail().isBlank()) {
            if (userRepository.existsByEmail(patchDto.getEmail()) && !patchDto.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new ConflictException("Email уже используется другим пользователем");
            }
        }
        UserMapper.merge(user, patchDto);
        User saved = userRepository.save(user);
        log.info("Обновлён пользователь id={}", id);
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден: id=" + id);
        }
        userRepository.deleteById(id);
        log.info("Удалён пользователь id={}", id);
    }
}