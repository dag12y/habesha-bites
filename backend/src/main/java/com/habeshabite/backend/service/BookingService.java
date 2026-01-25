package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.BookingRequest;
import com.habeshabite.backend.dto.BookingResponse;
import com.habeshabite.backend.dto.TableResponse;
import com.habeshabite.backend.entity.Booking;
import com.habeshabite.backend.entity.Table;
import com.habeshabite.backend.entity.User;
import com.habeshabite.backend.repository.BookingRepository;
import com.habeshabite.backend.repository.TableRepository;
import com.habeshabite.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                         TableRepository tableRepository,
                         UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public List<TableResponse> getAllTables() {
        List<Table> tables = tableRepository.findByIsActiveTrue();
        LocalDateTime now = LocalDateTime.now();
        
        return tables.stream().map(table -> {
            TableResponse response = new TableResponse();
            response.setId(table.getId());
            response.setTableNumber(table.getTableNumber());
            response.setCapacity(table.getCapacity());
            response.setIsActive(table.getIsActive());
            
            // Get all upcoming bookings for this table
            List<Booking> allUpcoming = bookingRepository.findUpcomingBookingsForTable(table, now);
            
            // Check if table is available for immediate booking
            // A booking at time T blocks the table from T-2 to T+2 (4 hour window)
            // If there's any booking in the next 4 hours, the table is unavailable
            // because the booking's blocked window would overlap with the next 2 hours
            boolean isAvailable = true;
            
            if (!allUpcoming.isEmpty()) {
                // Check if the next booking is within 4 hours
                // If booking is at T, it blocks from T-2 to T+2
                // For it to block "now" to "now+2", we need: T-2 <= now+2 AND T+2 >= now
                // Since T >= now (upcoming), this simplifies to: T <= now + 4
                LocalDateTime fourHoursLater = now.plusHours(4);
                
                for (Booking booking : allUpcoming) {
                    LocalDateTime bookingTime = booking.getBookingDateTime();
                    
                    // If booking is within 4 hours, it blocks the table
                    if (!bookingTime.isAfter(fourHoursLater)) {
                        isAvailable = false;
                        break;
                    }
                }
            }
            
            response.setIsAvailable(isAvailable);
            
            // Get upcoming bookings for display
            List<TableResponse.BookingInfo> bookingInfos = allUpcoming.stream()
                    .limit(5) // Show only next 5 bookings
                    .map(booking -> {
                        TableResponse.BookingInfo info = new TableResponse.BookingInfo();
                        info.setBookingId(booking.getId());
                        info.setBookingDateTime(booking.getBookingDateTime());
                        info.setNumberOfGuests(booking.getNumberOfGuests());
                        info.setUserName(booking.getUser().getFullName());
                        return info;
                    })
                    .collect(Collectors.toList());
            response.setUpcomingBookings(bookingInfos);
            
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long tableId = request.getTableId();
        if (tableId == null) {
            throw new RuntimeException("Table ID is required");
        }
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        if (!table.getIsActive()) {
            throw new RuntimeException("Table is not available for booking");
        }

        if (request.getNumberOfGuests() > table.getCapacity()) {
            throw new RuntimeException("Number of guests exceeds table capacity");
        }

            // Check if table is already booked at this time (within 2 hours window)
            LocalDateTime bookingTime = request.getBookingDateTime();
            if (bookingTime == null) {
                throw new RuntimeException("Booking date and time is required");
            }
            LocalDateTime twoHoursBefore = bookingTime.minusHours(2);
            LocalDateTime twoHoursAfter = bookingTime.plusHours(2);
            
            List<Booking> conflictingBookings = bookingRepository.findActiveBookingsForTableInTimeRange(
                    table, twoHoursBefore, twoHoursAfter);
        
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Table is already booked at this time. Please choose another time.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTable(table);
        booking.setBookingDateTime(bookingTime);
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserOrderByBookingDateTimeDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        if (bookingId == null) {
            throw new RuntimeException("Booking ID is required");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if booking belongs to user (admins can cancel any booking)
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Non-admins: can only cancel if booking is more than 1 hour away
        if (user.getRole() != User.Role.ADMIN
                && booking.getBookingDateTime().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new RuntimeException("Cannot cancel booking less than 1 hour before the scheduled time");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserEmail(booking.getUser().getEmail());
        response.setUserName(booking.getUser().getFullName());
        response.setTableId(booking.getTable().getId());
        response.setTableNumber(booking.getTable().getTableNumber());
        response.setTableCapacity(booking.getTable().getCapacity());
        response.setBookingDateTime(booking.getBookingDateTime());
        response.setNumberOfGuests(booking.getNumberOfGuests());
        response.setSpecialRequests(booking.getSpecialRequests());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }
}
