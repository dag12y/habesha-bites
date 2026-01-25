package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.BookingRequest;
import com.habeshabite.backend.dto.BookingResponse;
import com.habeshabite.backend.dto.TableResponse;
import com.habeshabite.backend.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/tables")
    public ResponseEntity<List<TableResponse>> getAllTables() {
        List<TableResponse> tables = bookingService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            BookingResponse booking = bookingService.createBooking(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.habeshabite.backend.dto.ErrorResponse("Booking Error", e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        List<BookingResponse> bookings = bookingService.getUserBookings(userEmail);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            BookingResponse booking = bookingService.cancelBooking(bookingId, userEmail);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.habeshabite.backend.dto.ErrorResponse("Cancellation Error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(Authentication authentication) {
        // Only admins can see all bookings - this will be enforced by SecurityConfig
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
}
