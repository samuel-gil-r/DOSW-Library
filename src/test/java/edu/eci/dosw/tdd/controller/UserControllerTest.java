package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    MockMvc mockMvc;

    @Mock
    LibraryService libraryService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerUser_returns201() throws Exception {
        User user = new User("1", "Alice", "alice", null, "USER");
        when(libraryService.registerUser(any(), eq("Alice"), any(), any(), any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getAllUsers_returns200() throws Exception {
        when(libraryService.getAllUsers()).thenReturn(List.of(
                new User("1", "Alice", "alice", null, "USER"),
                new User("2", "Bob", "bob", null, "USER")
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUser_returns200() throws Exception {
        User user = new User("1", "Alice", "alice", null, "USER");
        when(libraryService.getUser("1")).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(libraryService.getUser(anyString()))
                .thenThrow(new UserNotFoundException("User not found: xyz"));

        mockMvc.perform(get("/api/users/xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found: xyz"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
