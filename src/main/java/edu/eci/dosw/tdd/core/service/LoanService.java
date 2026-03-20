package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;

import java.util.List;

public interface LoanService {
    LoanDTO createLoan(LoanDTO loanDTO);
    List<LoanDTO> getAllLoans();
    LoanDTO getLoanById(String id);
}
