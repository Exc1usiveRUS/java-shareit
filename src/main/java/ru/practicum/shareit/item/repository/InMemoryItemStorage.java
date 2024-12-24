package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmptyFieldException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class InMemoryItemStorage implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;
    private final InMemoryUserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        if ((itemDto.getName() == null || itemDto.getName().isBlank())
                || (itemDto.getDescription() == null || itemDto.getDescription().isBlank())) {
            throw new EmptyFieldException("Поля name и description не могут быть пустыми");
        }
        itemDto.setId(id);
        itemDto.setOwner(UserMapper.toUser(userStorage.getUser(userId)));
        items.put(itemDto.getId(), ItemMapper.toItem(itemDto, UserMapper.toUser(userStorage.getUser(userId))));
        id++;
        return itemDto;
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать предмет может только его владелец");
        }
        ItemDto updatedItem = ItemMapper.toItemDto(items.get(itemId));
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, ItemMapper.toItem(updatedItem, UserMapper.toUser(userStorage.getUser(userId))));
        return itemDto;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Удалить предмет может только его владелец");
        }
        items.remove(itemId);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String textToLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(textToLowerCase)
                        || item.getName().toLowerCase().contains(textToLowerCase))
                        && item.getOwner().getId().equals(userId)
                        && item.getAvailable() != null && item.getAvailable().booleanValue())
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
