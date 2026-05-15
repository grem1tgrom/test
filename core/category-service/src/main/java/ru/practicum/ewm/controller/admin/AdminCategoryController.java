package ru.practicum.ewm.controller.admin;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody CategoryDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long catId) {
        service.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@Positive @PathVariable Long catId, @RequestBody CategoryDto dto) {
        return service.update(catId, dto);
    }
}