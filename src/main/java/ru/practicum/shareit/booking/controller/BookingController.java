package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @Valid @RequestBody BookingNewDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
       return bookingService.findBookingByIdAndBookerIdOrOwnerId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingsByState(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                               @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsByState(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsByOwnerId(ownerId, state);
    }

}
