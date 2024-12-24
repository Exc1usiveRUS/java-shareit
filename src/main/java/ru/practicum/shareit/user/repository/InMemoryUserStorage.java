package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailValidException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    // чтобы ускорить код и не проходиться циклом по всем пользователям добавил сет email
    private final Set<String> emails = new HashSet<>();
    private Long id = 1L;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new EmailValidException("Пользователь с таким email уже существует");
        }
        emails.add(userDto.getEmail());
        userDto.setId(id);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        id++;
        return userDto;
    }

    @Override
    public UserDto getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        UserDto updatedUser = UserMapper.toUserDto(users.get(userId));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (emails.contains(userDto.getEmail())) {
            throw new EmailValidException("Пользователь с таким email уже существует");
        }
        if (userDto.getEmail() != null) {
            emails.remove(users.get(userId).getEmail());
            emails.add(userDto.getEmail());
            updatedUser.setEmail(userDto.getEmail());
        }
        users.put(userId, UserMapper.toUser(updatedUser));
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        users.remove(userId);
    }
}
