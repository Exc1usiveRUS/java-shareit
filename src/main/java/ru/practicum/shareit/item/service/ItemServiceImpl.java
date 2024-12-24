package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.InMemoryItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemStorage itemStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        return itemStorage.getItem(userId, itemId);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemStorage.deleteItem(userId, itemId);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        return itemStorage.searchItems(userId, text);
    }
}
