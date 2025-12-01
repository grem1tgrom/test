package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto dto) {
        log.debug("POST /users body={}", dto);
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.debug("GET /users/{}", id);
        return service.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("GET /users");
        return service.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto patchDto) {
        log.debug("PATCH /users/{} patch={}", id, patchDto);
        return service.update(id, patchDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.debug("DELETE /users/{}", id);
        service.delete(id);
    }
}