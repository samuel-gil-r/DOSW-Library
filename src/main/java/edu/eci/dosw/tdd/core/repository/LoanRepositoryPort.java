package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepositoryPort {
    Loan save(Loan loan);
    Optional<Loan> findById(String id);
    List<Loan> findAll();
    List<Loan> findByUserId(String userId);
    void delete(String id);
    long countActiveByUserId(String userId);
}
