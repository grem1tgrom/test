package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody ItemDto dto) {
        log.debug("POST /items userId={} body={}", userId, dto);
        return service.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patchDto) {
        log.debug("PATCH /items/{} userId={} patch={}", itemId, userId, patchDto);
        return service.update(userId, itemId, patchDto);
    }

    @GetMapping("/{itemId}")
    public ItemDetailsDto get(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long itemId) {
        log.debug("GET /items/{} userId={}", itemId, userId);
        return service.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDetailsDto> getOwnerItems(@RequestHeader(USER_HEADER) Long userId) {
        log.debug("GET /items userId={}", userId);
        return service.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        log.debug("GET /items/search text='{}'", text);
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto comment) {
        log.debug("POST /items/{}/comment userId={} body={}", itemId, userId, comment);
        return service.addComment(userId, itemId, comment);
    }
}