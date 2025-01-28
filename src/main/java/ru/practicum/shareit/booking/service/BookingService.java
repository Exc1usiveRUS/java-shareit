package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(Long bookerId, BookingNewDto bookingDto) {
        User booker = getUser(bookerId);
        Item item = getItem(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new RuntimeException("Предмет уже кем-то забронирован");
        }

        List<Booking> bookings = bookingRepository.findAllWithIntersectionDates(bookingDto.getItemId(),
                Set.of(BookingStatus.APPROVED), bookingDto.getStart(), bookingDto.getEnd());

        if (!bookings.isEmpty()) {
            throw new NotFoundException("Предмет занят в указанные даты");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto updateBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Пользователь не является владельцем предмета");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Бронирование уже подтверждено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto findBookingByIdAndBookerIdOrOwnerId(Long bookerId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.getBooker().getId().equals(bookerId)
                        || booking.getItem().getOwner().getId().equals(bookerId))
                .map(BookingMapper::toBookingDto)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
    }

    public List<BookingDto> findBookingsByState(Long bookerId, BookingState state) {
        User user = getUser(bookerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                    BookingStatus.REJECTED);
        };

        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream().map(BookingMapper::toBookingDto).toList();
        }
    }

    public List<BookingDto> findBookingsByOwnerId(Long ownerId, BookingState state) {
        User user = getUser(ownerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.REJECTED);
        };

        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream().map(BookingMapper::toBookingDto).toList();
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с таким id не найден"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
    }
}
