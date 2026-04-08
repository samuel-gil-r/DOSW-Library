package edu.eci.dosw.tdd;

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
import edu.eci.dosw.tdd.core.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock BookRepositoryPort bookRepository;
    @Mock UserRepositoryPort userRepository;
    @Mock LoanRepositoryPort loanRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks LibraryService libraryService;

    private final String BOOK_ID = UUID.randomUUID().toString();
    private final String USER_ID = UUID.randomUUID().toString();
    private final String LOAN_ID = UUID.randomUUID().toString();

    private Book book(String id, int total, int available) {
        return new Book(id, "Test Book", "Author", total, available);
    }

    private User user(String id) {
        return new User(id, "Alice", "alice", "encoded", "USER");
    }

    private Loan loan(String id, String bookId, String userId, LoanStatus status) {
        return new Loan(id, bookId, userId, LocalDate.now(), null, status);
    }

    @Test
    void addBook_shouldReturnBookWithId() {
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Book book = libraryService.addBook(null, "Clean Code", "Robert C. Martin", 5);
        assertNotNull(book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals(5, book.getTotalStock());
        assertEquals(5, book.getAvailableStock());
    }

    @Test
    void getBook_shouldReturnBookById() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book(BOOK_ID, 5, 5)));
        Book found = libraryService.getBook(BOOK_ID);
        assertEquals(BOOK_ID, found.getId());
    }

    @Test
    void getBook_shouldThrowWhenNotFound() {
        when(bookRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> libraryService.getBook(UUID.randomUUID().toString()));
    }

    @Test
    void getAllBooks_shouldReturnAllAddedBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                book(UUID.randomUUID().toString(), 5, 5),
                book(UUID.randomUUID().toString(), 3, 3)
        ));
        assertEquals(2, libraryService.getAllBooks().size());
    }

    @Test
    void registerUser_shouldReturnUserWithId() {
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        User user = libraryService.registerUser(null, "Alice", "alice", "pass", "USER");
        assertNotNull(user.getId());
        assertEquals("Alice", user.getName());
    }

    @Test
    void getUser_shouldReturnUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));
        User found = libraryService.getUser(USER_ID);
        assertEquals(USER_ID, found.getId());
    }

    @Test
    void getUser_shouldThrowWhenNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> libraryService.getUser(UUID.randomUUID().toString()));
    }

    @Test
    void loanBook_shouldCreateActiveLoan() {
        Book book = book(BOOK_ID, 5, 5);
        User user = user(USER_ID);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanRepository.countActiveByUserId(USER_ID)).thenReturn(0L);
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan loan = libraryService.loanBook(BOOK_ID, USER_ID);
        assertNotNull(loan.getId());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(BOOK_ID, loan.getBookId());
    }

    @Test
    void loanBook_shouldThrowWhenBookNotAvailable() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book(BOOK_ID, 1, 0)));
        assertThrows(BookNotAvailableException.class,
                () -> libraryService.loanBook(BOOK_ID, USER_ID));
    }

    @Test
    void loanBook_shouldThrowWhenLoanLimitExceeded() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book(BOOK_ID, 5, 5)));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));
        when(loanRepository.countActiveByUserId(USER_ID)).thenReturn(3L);
        assertThrows(LoanLimitExceededException.class,
                () -> libraryService.loanBook(BOOK_ID, USER_ID));
    }

    @Test
    void returnBook_shouldMarkLoanReturnedAndMakeBookAvailable() {
        Loan activeLoan = loan(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE);
        Book book = book(BOOK_ID, 5, 4);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan returned = libraryService.returnBook(LOAN_ID);
        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
    }

    @Test
    void getLoansByUser_shouldReturnOnlyUserLoans() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));
        when(loanRepository.findByUserId(USER_ID)).thenReturn(List.of(loan(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE)));

        List<Loan> loans = libraryService.getLoansByUser(USER_ID);
        assertEquals(1, loans.size());
        assertEquals(BOOK_ID, loans.get(0).getBookId());
    }

    @Test
    void getAllLoans_shouldReturnAllLoans() {
        when(loanRepository.findAll()).thenReturn(List.of(
                loan(UUID.randomUUID().toString(), BOOK_ID, USER_ID, LoanStatus.ACTIVE),
                loan(UUID.randomUUID().toString(), BOOK_ID, USER_ID, LoanStatus.ACTIVE)
        ));
        assertEquals(2, libraryService.getAllLoans().size());
    }

    @Test
    void getLoanById_shouldReturnLoan() {
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE)));
        Loan found = libraryService.getLoanById(LOAN_ID);
        assertEquals(LOAN_ID, found.getId());
    }

    @Test
    void getLoanById_shouldThrowWhenNotFound() {
        when(loanRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> libraryService.getLoanById(UUID.randomUUID().toString()));
    }

    @Test
    void getAllUsers_shouldReturnAllRegisteredUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                user(UUID.randomUUID().toString()),
                user(UUID.randomUUID().toString())
        ));
        assertEquals(2, libraryService.getAllUsers().size());
    }
}
