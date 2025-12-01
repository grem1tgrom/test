package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItem(Item item);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findByOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start <= ?2 and b.end >= ?3")
    List<Booking> findOwnerCurrent(Long ownerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findOwnerPast(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findOwnerFuture(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findOwnerByStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("select (count(b) > 0) from Booking b " +
            "where b.item.id = ?1 and b.booker.id = ?2 and b.end <= ?3 and b.status = 'APPROVED'")
    boolean existsFinishedBookingForUser(Long itemId, Long userId, LocalDateTime now);
}