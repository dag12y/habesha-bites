package com.habeshabite.backend.repository;

import com.habeshabite.backend.entity.Booking;
import com.habeshabite.backend.entity.Table;
import com.habeshabite.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingDateTimeDesc(User user);
    List<Booking> findByTableOrderByBookingDateTimeAsc(Table table);
    
    @Query("SELECT b FROM Booking b WHERE b.table = :table AND b.bookingDateTime >= :startTime AND b.bookingDateTime < :endTime AND b.status != 'CANCELLED'")
    List<Booking> findActiveBookingsForTableInTimeRange(
            @Param("table") Table table,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT b FROM Booking b WHERE b.table = :table AND b.bookingDateTime >= :now AND b.status != 'CANCELLED' ORDER BY b.bookingDateTime ASC")
    List<Booking> findUpcomingBookingsForTable(@Param("table") Table table, @Param("now") LocalDateTime now);
}
