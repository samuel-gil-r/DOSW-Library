package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {
    List<LoanEntity> findByUserId(UUID userId);
    long countByUserIdAndStatus(UUID userId, LoanStatus status);
}
