package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;

import java.util.UUID;

public class LoanEntityMapper {

    public static Loan toDomain(LoanEntity entity) {
        return new Loan(
                entity.getId().toString(),
                entity.getBookId().toString(),
                entity.getUserId().toString(),
                entity.getLoanDate(),
                entity.getReturnDate(),
                entity.getStatus()
        );
    }

    public static LoanEntity toEntity(Loan loan) {
        return new LoanEntity(
                UUID.fromString(loan.getId()),
                UUID.fromString(loan.getBookId()),
                UUID.fromString(loan.getUserId()),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}
