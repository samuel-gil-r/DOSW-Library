package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    @Schema(example = "B001")
    private String id;

    @Schema(example = "Clean Code")
    private String title;

    @Schema(example = "Robert C. Martin")
    private String author;
}
