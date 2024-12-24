package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.InMemoryItemStorage;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final InMemoryItemStorage inMemoryItemStorage;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return inMemoryItemStorage.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        return inMemoryItemStorage.getItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable @Valid Long itemId,
                              @RequestBody ItemDto itemDto) {
        return inMemoryItemStorage.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        inMemoryItemStorage.deleteItem(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItems(@RequestHeader(HEADER_USER_ID) Long userId) {
        return inMemoryItemStorage.getAllItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @RequestParam(required = false) String text) {
        return inMemoryItemStorage.searchItems(userId, text);
    }
}
