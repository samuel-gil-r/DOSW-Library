package edu.eci.dosw.tdd.persistence.entity;

import edu.eci.dosw.tdd.core.model.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    private UUID id;
    private UUID bookId;
    private UUID userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}
