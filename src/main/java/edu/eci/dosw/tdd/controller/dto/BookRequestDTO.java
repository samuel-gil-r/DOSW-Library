package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {

    @Schema(example = "")
    private String id;

    @Schema(example = "Clean Code")
    private String title;

    @Schema(example = "Robert C. Martin")
    private String author;

    @Schema(example = "5")
    private int totalStock;
}
