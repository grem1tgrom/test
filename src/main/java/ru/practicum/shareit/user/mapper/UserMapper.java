package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toModel(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static void merge(User existing, UserDto patch) {
        if (patch.getName() != null && !patch.getName().isBlank()) existing.setName(patch.getName());
        if (patch.getEmail() != null && !patch.getEmail().isBlank()) existing.setEmail(patch.getEmail());
    }
}