package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperTest {

    private final LoanMapper mapper = new LoanMapper();

    @Test
    void toDTO_returnsNullWhenLoanIsNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_mapsAllFields() {
        LocalDate now = LocalDate.now();
        Loan loan = new Loan("1", "book-1", "user-1", now, now, LoanStatus.ACTIVE);
        LoanDTO dto = mapper.toDTO(loan);
        assertEquals("book-1", dto.getBookId());
        assertEquals("user-1", dto.getUserId());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals(now, dto.getLoanDate());
    }

    @Test
    void toModel_returnsNullWhenDtoIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_mapsDateAndStatus() {
        LocalDate now = LocalDate.now();
        LoanDTO dto = new LoanDTO("book-1", "user-1", now, now, "ACTIVE");
        Loan loan = mapper.toModel(dto);
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(now, loan.getLoanDate());
        assertEquals(now, loan.getReturnDate());
    }
}
