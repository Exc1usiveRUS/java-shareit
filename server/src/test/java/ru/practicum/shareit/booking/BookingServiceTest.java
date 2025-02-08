package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private BookingNewDto bookingNewDto;
    private BookingDto bookingDto;
    private Long userId;
    private Long itemId;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Name");
        user.setEmail("test@mail.ru");
        userRepository.save(user);
        userId = user.getId();

        item = new Item();
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);
        itemId = item.getId();

        bookingNewDto = new BookingNewDto();
        bookingNewDto.setItemId(itemId);
        bookingNewDto.setStart(LocalDateTime.now().plusHours(1));
        bookingNewDto.setEnd(LocalDateTime.now().plusHours(2));

        bookingDto = bookingDto.builder()
                .id(1L)
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        assertNotNull(bookingDto);
        assertEquals(bookingNewDto.getStart(), bookingDto.getStart());
        assertEquals(bookingNewDto.getEnd(), bookingDto.getEnd());
        assertEquals(itemId, bookingDto.getItem().getId());
        assertEquals(userId, bookingDto.getBooker().getId());
    }

    @Test
    void findBookingById() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        BookingDto bookingDtoById = bookingService.findBookingByIdAndBookerIdOrOwnerId(userId, bookingDto.getId());
        assertNotNull(bookingDtoById);
        assertEquals(bookingDto.getId(), bookingDtoById.getId());
        assertEquals(bookingDto.getStart(), bookingDtoById.getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoById.getEnd());
        assertEquals(bookingDto.getItem().getId(), bookingDtoById.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), bookingDtoById.getBooker().getId());
    }

    @Test
    void findBookingsByBookerId() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        List<BookingDto> bookingDtoList = bookingService.findBookingsByState(userId, BookingState.ALL);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingDto.getId(), bookingDtoList.getFirst().getId());
        assertEquals(bookingDto.getStart(), bookingDtoList.getFirst().getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoList.getFirst().getEnd());
        assertEquals(bookingDto.getItem().getId(), bookingDtoList.getFirst().getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), bookingDtoList.getFirst().getBooker().getId());
    }

    @Test
    void findBookingsByOwnerId() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        List<BookingDto> bookingDtoList = bookingService.findBookingsByOwnerId(userId, BookingState.ALL);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingDto.getId(), bookingDtoList.getFirst().getId());
        assertEquals(bookingDto.getStart(), bookingDtoList.getFirst().getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoList.getFirst().getEnd());
        assertEquals(bookingDto.getItem().getId(), bookingDtoList.getFirst().getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), bookingDtoList.getFirst().getBooker().getId());
    }

    @Test
    void findBookingsByState() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        List<BookingDto> bookingDtoList = bookingService.findBookingsByState(userId, BookingState.ALL);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingDto.getId(), bookingDtoList.getFirst().getId());
        assertEquals(bookingDto.getStart(), bookingDtoList.getFirst().getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoList.getFirst().getEnd());
        assertEquals(bookingDto.getItem().getId(), bookingDtoList.getFirst().getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), bookingDtoList.getFirst().getBooker().getId());
    }

    @Test
    void updateBooking() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        BookingDto bookingDtoUpdated = bookingService.updateBooking(userId, bookingDto.getId(), true);
        assertNotNull(bookingDtoUpdated);
        assertEquals(bookingDto.getId(), bookingDtoUpdated.getId());
        assertEquals(bookingDto.getStart(), bookingDtoUpdated.getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoUpdated.getEnd());
        assertEquals(bookingDto.getItem().getId(), bookingDtoUpdated.getItem().getId());
        assertEquals(bookingDto.getBooker().getId(), bookingDtoUpdated.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingDtoUpdated.getStatus());
    }

    @Test
    void getByIdWithInvalidUserId() {
        BookingDto bookingDto = bookingService.createBooking(userId, bookingNewDto);
        assertThrows(NotFoundException.class, () -> bookingService.findBookingByIdAndBookerIdOrOwnerId(99L, bookingDto.getId()));
    }

    @Test
    void createBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(userId, bookingNewDto));
    }
}
