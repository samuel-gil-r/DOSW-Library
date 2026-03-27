package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.UserRequestDTO;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LibraryService libraryService;

    @Operation(summary = "Register a new user")
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody UserRequestDTO body) {
        User user = libraryService.registerUser(body.getId(), body.getName(), body.getUsername(), body.getPassword(), body.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(libraryService.getAllUsers());
    }

    @Operation(summary = "Get a user by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(libraryService.getUser(id));
    }
}
