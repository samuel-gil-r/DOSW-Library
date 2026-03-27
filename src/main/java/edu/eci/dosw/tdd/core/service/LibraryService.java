package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookEntityMapper;
import edu.eci.dosw.tdd.persistence.mapper.LoanEntityMapper;
import edu.eci.dosw.tdd.persistence.mapper.UserEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import edu.eci.dosw.tdd.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Book addBook(String id, String title, String author, int totalStock) {
        ValidationUtil.requireNonBlank(title, "title");
        ValidationUtil.requireNonBlank(author, "author");
        String bookId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        BookEntity entity = new BookEntity(UUID.fromString(bookId), title, author, totalStock, totalStock);
        bookRepository.save(entity);
        return BookEntityMapper.toDomain(entity);
    }

    public Book getBook(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        BookEntity entity = bookRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
        return BookEntityMapper.toDomain(entity);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public User registerUser(String id, String name, String username, String rawPassword, String role) {
        ValidationUtil.requireNonBlank(name, "name");
        ValidationUtil.requireNonBlank(username, "username");
        ValidationUtil.requireNonBlank(rawPassword, "password");
        String userId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        UserRole userRole = (role != null) ? UserRole.valueOf(role.toUpperCase()) : UserRole.USER;
        UserEntity entity = new UserEntity(UUID.fromString(userId), name, username, encodedPassword, userRole);
        userRepository.save(entity);
        return UserEntityMapper.toDomain(entity);
    }

    public User getUser(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        UserEntity entity = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        return UserEntityMapper.toDomain(entity);
    }

    public User getUserByUsername(String username) {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return UserEntityMapper.toDomain(entity);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public Loan loanBook(String bookId, String userId) {
        BookEntity bookEntity = bookRepository.findById(UUID.fromString(bookId))
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));
        if (bookEntity.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book is not available: " + bookId);
        }
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        long activeLoans = loanRepository.countByUserIdAndStatus(UUID.fromString(userId), LoanStatus.ACTIVE);
        if (activeLoans >= 3) {
            throw new LoanLimitExceededException("Loan limit exceeded for user: " + userId);
        }
        bookEntity.setAvailableStock(bookEntity.getAvailableStock() - 1);
        bookRepository.save(bookEntity);
        LoanEntity loanEntity = new LoanEntity(
                UUID.randomUUID(), UUID.fromString(bookId), UUID.fromString(userId),
                LocalDate.now(), null, LoanStatus.ACTIVE
        );
        loanRepository.save(loanEntity);
        return LoanEntityMapper.toDomain(loanEntity);
    }

    @Transactional
    public Loan returnBook(String loanId) {
        ValidationUtil.requireNonBlank(loanId, "loanId");
        LoanEntity loanEntity = loanRepository.findById(UUID.fromString(loanId))
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        if (loanEntity.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }
        loanEntity.setStatus(LoanStatus.RETURNED);
        loanEntity.setReturnDate(LocalDate.now());
        loanRepository.save(loanEntity);
        bookRepository.findById(loanEntity.getBookId()).ifPresent(book -> {
            book.setAvailableStock(Math.min(book.getAvailableStock() + 1, book.getTotalStock()));
            bookRepository.save(book);
        });
        return LoanEntityMapper.toDomain(loanEntity);
    }

    public List<Loan> getLoansByUser(String userId) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return loanRepository.findByUserId(UUID.fromString(userId)).stream()
                .map(LoanEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(LoanEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Loan getLoanById(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return loanRepository.findById(UUID.fromString(id))
                .map(LoanEntityMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }
}
