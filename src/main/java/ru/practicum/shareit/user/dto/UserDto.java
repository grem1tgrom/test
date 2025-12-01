package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
}