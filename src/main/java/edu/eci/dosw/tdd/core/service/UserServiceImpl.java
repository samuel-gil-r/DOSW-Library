package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, userDTO.getName());
        users.put(id, user);
        return toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return users.values().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(String id) {
        return toDTO(findUser(id));
    }

    User findUser(String id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + id);
        }
        return user;
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName());
    }
}
