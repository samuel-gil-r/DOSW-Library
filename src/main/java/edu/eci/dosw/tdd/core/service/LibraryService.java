package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final Map<Book, Integer> books = new HashMap<>();
    private final List<User> users = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public Book addBook(String id, String title, String author) {
        ValidationUtil.requireNonBlank(title, "title");
        ValidationUtil.requireNonBlank(author, "author");
        String bookId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        Book book = new Book(bookId, title, author, true);
        books.put(book, 1);
        return book;
    }

    public Book getBook(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return books.keySet().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.keySet());
    }

    public User registerUser(String id, String name) {
        ValidationUtil.requireNonBlank(name, "name");
        String userId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();
        User user = new User(userId, name);
        users.add(user);
        return user;
    }

    public User getUser(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Loan loanBook(String bookId, String userId) {
        Book book = getBook(bookId);
        User user = getUser(userId);
        int copies = books.getOrDefault(book, 0);
        if (copies <= 0) {
            throw new BookNotAvailableException("Book is not available: " + bookId);
        }
        long activeLoans = loans.stream()
                .filter(l -> l.getUser().getId().equals(userId) && "ACTIVE".equals(l.getStatus()))
                .count();
        if (activeLoans >= 3) {
            throw new LoanLimitExceededException("Loan limit exceeded for user: " + userId);
        }
        books.put(book, copies - 1);
        book.setAvailable(copies - 1 > 0);
        Loan loan = new Loan(UUID.randomUUID().toString(), book, user, LocalDate.now(), null, "ACTIVE");
        loans.add(loan);
        return loan;
    }

    public Loan returnBook(String loanId) {
        ValidationUtil.requireNonBlank(loanId, "loanId");
        Loan loan = loans.stream()
                .filter(l -> l.getId().equals(loanId) && "ACTIVE".equals(l.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Active loan not found: " + loanId));
        loan.setStatus("RETURNED");
        loan.setReturnDate(LocalDate.now());
        books.computeIfPresent(loan.getBook(), (b, count) -> count + 1);
        loan.getBook().setAvailable(true);
        return loan;
    }

    public List<Loan> getLoansByUser(String userId) {
        getUser(userId);
        return loans.stream()
                .filter(l -> l.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public Loan getLoanById(String id) {
        ValidationUtil.requireNonBlank(id, "id");
        return loans.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }
}
