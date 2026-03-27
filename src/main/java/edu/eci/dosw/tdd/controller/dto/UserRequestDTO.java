package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @Schema(example = "")
    private String id;

    @Schema(example = "Samuel Gil")
    private String name;

    @Schema(example = "samuel.gil")
    private String username;

    @Schema(example = "password123")
    private String password;

    @Schema(example = "USER")
    private String role;
}
