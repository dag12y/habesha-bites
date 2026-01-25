package com.habeshabite.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {

    private Long id;
    private Integer tableNumber;
    private Integer capacity;
    private Boolean isActive;
    private Boolean isAvailable; // whether table is available for booking at current time
    private List<BookingInfo> upcomingBookings; // upcoming bookings for this table

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long bookingId;
        private LocalDateTime bookingDateTime;
        private Integer numberOfGuests;
        private String userName;
    }
}
