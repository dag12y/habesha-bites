package com.habeshabite.backend.dto;

import com.habeshabite.backend.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long tableId;
    private Integer tableNumber;
    private Integer tableCapacity;
    private LocalDateTime bookingDateTime;
    private Integer numberOfGuests;
    private String specialRequests;
    private Booking.BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
