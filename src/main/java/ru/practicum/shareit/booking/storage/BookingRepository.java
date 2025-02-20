package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query(nativeQuery = true, value
            = "SELECT * FROM (" +
                "SELECT *, ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.end_date DESC) AS anchor " +
                "FROM bookings b " +
                "WHERE (b.end_date < ?1 OR b.start_date < ?1 AND b.end_date > ?1) AND b.item_id IN (?2)" +
            ") WHERE anchor = 1")
    List<Booking> findAllLastBookingsForItems(LocalDateTime dateTime, List<Long> items);

    @Query(nativeQuery = true, value
            = "SELECT * FROM (" +
                "SELECT *, ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS anchor " +
                "FROM bookings b " +
                "WHERE b.start_date > ?1 AND b.item_id IN (?2)" +
            ") WHERE anchor = 1")
    List<Booking> findAllNearestNextBookingsForItemsMap(LocalDateTime dateTime, List<Long> items);
}
