package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(String id);
}
