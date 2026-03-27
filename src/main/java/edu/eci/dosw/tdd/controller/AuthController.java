package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.AuthResponse;
import edu.eci.dosw.tdd.controller.dto.LoginRequest;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.LibraryService;
import edu.eci.dosw.tdd.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Authentication endpoints")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LibraryService libraryService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Login and obtain JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = libraryService.getUserByUsername(request.getUsername());
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, "Invalid credentials"));
            }
            String token = jwtService.generateToken(user.getId(), user.getRole());
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid credentials"));
        }
    }
}
