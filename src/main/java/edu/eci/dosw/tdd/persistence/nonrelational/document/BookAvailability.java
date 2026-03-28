package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAvailability {

    private String status;
    private int totalCopies;
    private int availableCopies;
    private int borrowedCopies;
}
