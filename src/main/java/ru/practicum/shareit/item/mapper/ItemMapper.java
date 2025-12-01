package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static ItemDetailsDto toDetailsDto(Item item) {
        if (item == null) return null;
        ItemDetailsDto dto = new ItemDetailsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static Item toModel(ItemDto dto, User owner) {
        if (dto == null) return null;
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static void merge(Item existing, ItemDto patch) {
        if (patch.getName() != null && !patch.getName().isBlank()) existing.setName(patch.getName());
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) existing.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) existing.setAvailable(patch.getAvailable());
    }
}