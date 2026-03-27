package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final List<Loan> loans = new ArrayList<>();
    private final BookServiceImpl bookService;
    private final UserServiceImpl userService;

    public LoanServiceImpl(BookServiceImpl bookService, UserServiceImpl userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @Override
    public LoanDTO createLoan(LoanDTO loanDTO) {
        Book book = bookService.findBook(loanDTO.getBookId());
        User user = userService.findUser(loanDTO.getUserId());

        if (book.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book is not available: " + book.getId());
        }
        long activeLoans = loans.stream()
                .filter(l -> l.getUserId().equals(user.getId()) && LoanStatus.ACTIVE == l.getStatus())
                .count();
        if (activeLoans >= 3) {
            throw new LoanLimitExceededException("User has reached the loan limit: " + user.getId());
        }

        book.setAvailableStock(Math.max(0, book.getAvailableStock() - 1));
        Loan loan = new Loan(UUID.randomUUID().toString(), book.getId(), user.getId(),
                LocalDate.now(), loanDTO.getReturnDate(), LoanStatus.ACTIVE);
        loans.add(loan);
        return toDTO(loan);
    }

    @Override
    public List<LoanDTO> getAllLoans() {
        return loans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public LoanDTO getLoanById(String id) {
        return loans.stream()
                .filter(l -> l.getId().equals(id))
                .map(this::toDTO)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }

    private LoanDTO toDTO(Loan loan) {
        return new LoanDTO(loan.getBookId(), loan.getUserId(),
                loan.getLoanDate(), loan.getReturnDate(), loan.getStatus().name());
    }
}
