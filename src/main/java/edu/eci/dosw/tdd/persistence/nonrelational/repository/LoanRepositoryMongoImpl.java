package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("mongo")
@RequiredArgsConstructor
public class LoanRepositoryMongoImpl implements LoanRepositoryPort {

    private final MongoLoanRepository mongoLoanRepository;

    @Override
    public Loan save(Loan loan) {
        LoanDocument doc = mongoLoanRepository.findById(loan.getId())
                .orElse(new LoanDocument());
        doc.setId(loan.getId());
        doc.setBookId(loan.getBookId());
        doc.setUserId(loan.getUserId());
        doc.setLoanDate(loan.getLoanDate());
        doc.setReturnDate(loan.getReturnDate());
        doc.setStatus(loan.getStatus().name());
        return toDomain(mongoLoanRepository.save(doc));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return mongoLoanRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return mongoLoanRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return mongoLoanRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        mongoLoanRepository.deleteById(id);
    }

    @Override
    public long countActiveByUserId(String userId) {
        return mongoLoanRepository.findByUserId(userId).stream()
                .filter(l -> LoanStatus.ACTIVE.name().equals(l.getStatus()))
                .count();
    }

    private Loan toDomain(LoanDocument doc) {
        return new Loan(doc.getId(), doc.getBookId(), doc.getUserId(),
                doc.getLoanDate(), doc.getReturnDate(),
                doc.getStatus() != null ? LoanStatus.valueOf(doc.getStatus()) : null);
    }
}
