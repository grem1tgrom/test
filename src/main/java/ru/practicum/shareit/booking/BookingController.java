package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId,
                             @Valid @RequestBody BookingRequestDto request) {
        log.debug("POST /bookings userId={} body={}", userId, request);
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam("approved") boolean approved) {
        log.debug("PATCH /bookings/{}?approved={} ownerId={}", bookingId, approved, ownerId);
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long bookingId) {
        log.debug("GET /bookings/{} userId={}", bookingId, userId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBooker(@RequestHeader(USER_HEADER) Long userId,
                                        @RequestParam(name = "state", required = false, defaultValue = "ALL") BookingState state) {
        log.debug("GET /bookings userId={} state={}", userId, state);
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                       @RequestParam(name = "state", required = false, defaultValue = "ALL") BookingState state) {
        log.debug("GET /bookings/owner ownerId={} state={}", ownerId, state);
        return bookingService.getByOwner(ownerId, state);
    }
}