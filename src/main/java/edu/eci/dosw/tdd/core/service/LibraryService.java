package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepositoryPort bookRepository;
    private final UserRepositoryPort userRepository;
    private final LoanRepositoryPort loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Book addBook(String id, String title, String author, int totalStock) {
        ValidationUtil.requireNonBlank(title, "title");
        ValidationUtil.requireNonBlank(author, "author");
        String bookId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        return bookRepository.save(new Book(bookId, title, author, totalStock, totalStock));
    }

    public Book getBook(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public User registerUser(String id, String name, String username, String rawPassword, String role) {
        ValidationUtil.requireNonBlank(name, "name");
        ValidationUtil.requireNonBlank(username, "username");
        ValidationUtil.requireNonBlank(rawPassword, "password");
        String userId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        String userRole = (role != null) ? role.toUpperCase() : "USER";
        User user = new User(userId, name, username, passwordEncoder.encode(rawPassword), userRole);
        return userRepository.save(user);
    }

    public User getUser(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Loan loanBook(String bookId, String userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));
        if (book.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book is not available: " + bookId);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        if (loanRepository.countActiveByUserId(userId) >= 3) {
            throw new LoanLimitExceededException("Loan limit exceeded for user: " + userId);
        }
        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.save(book);
        return loanRepository.save(
                new Loan(UUID.randomUUID().toString(), bookId, userId, LocalDate.now(), null, LoanStatus.ACTIVE));
    }

    @Transactional
    public Loan returnBook(String loanId) {
        ValidationUtil.requireNonBlank(loanId, "loanId");
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);
        bookRepository.findById(loan.getBookId()).ifPresent(book -> {
            book.setAvailableStock(Math.min(book.getAvailableStock() + 1, book.getTotalStock()));
            bookRepository.save(book);
        });
        return loan;
    }

    public List<Loan> getLoansByUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }
}
