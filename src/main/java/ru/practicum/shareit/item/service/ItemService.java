package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);
    ItemDto update(Long ownerId, Long itemId, ItemDto patchDto);
    ItemDetailsDto get(Long requesterId, Long itemId);
    List<ItemDetailsDto> getOwnerItems(Long ownerId);
    List<ItemDto> search(String text);
    CommentDto addComment(Long userId, Long itemId, CommentDto comment);
}