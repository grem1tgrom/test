package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingRequestDto request);
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);
    BookingDto get(Long userId, Long bookingId);
    List<BookingDto> getByBooker(Long userId, BookingState state);
    List<BookingDto> getByOwner(Long ownerId, BookingState state);
}