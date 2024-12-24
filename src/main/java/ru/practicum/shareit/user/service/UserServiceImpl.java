package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage userStorage;

    @Override
    public UserDto getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
