package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    void testRegisterUser_success() {
        UserDTO input = new UserDTO(null, "Alice");
        UserDTO result = userService.registerUser(input);

        assertNotNull(result.getId());
        assertEquals("Alice", result.getName());
    }

    @Test
    void testGetUserById_notFound() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("nonexistent-id"));
    }
}
