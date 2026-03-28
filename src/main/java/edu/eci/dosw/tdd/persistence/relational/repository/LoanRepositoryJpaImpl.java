package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.mapper.LoanEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("relational")
@RequiredArgsConstructor
public class LoanRepositoryJpaImpl implements LoanRepositoryPort {

    private final LoanRepository loanRepository;

    @Override
    public Loan save(Loan loan) {
        return LoanEntityMapper.toDomain(loanRepository.save(LoanEntityMapper.toEntity(loan)));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return loanRepository.findById(UUID.fromString(id))
                .map(LoanEntityMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll().stream()
                .map(LoanEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return loanRepository.findByUserId(UUID.fromString(userId)).stream()
                .map(LoanEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        loanRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public long countActiveByUserId(String userId) {
        return loanRepository.countByUserIdAndStatus(UUID.fromString(userId), LoanStatus.ACTIVE);
    }
}
