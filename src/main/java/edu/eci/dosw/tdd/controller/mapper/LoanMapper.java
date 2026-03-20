package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanDTO toDTO(Loan loan) {
        if (loan == null) return null;
        return new LoanDTO(loan.getBook().getId(), loan.getUser().getId(),
                loan.getLoanDate(), loan.getReturnDate(), loan.getStatus());
    }

    public Loan toModel(LoanDTO dto) {
        if (dto == null) return null;
        Loan loan = new Loan();
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setStatus(dto.getStatus());
        return loan;
    }
}
