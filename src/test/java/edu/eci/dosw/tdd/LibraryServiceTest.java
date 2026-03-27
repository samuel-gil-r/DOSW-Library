package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.model.UserRole;
import edu.eci.dosw.tdd.core.service.LibraryService;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
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

    @Mock BookRepository bookRepository;
    @Mock UserRepository userRepository;
    @Mock LoanRepository loanRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks LibraryService libraryService;

    private final UUID BOOK_ID = UUID.randomUUID();
    private final UUID USER_ID = UUID.randomUUID();
    private final UUID LOAN_ID = UUID.randomUUID();

    private BookEntity bookEntity(UUID id, int total, int available) {
        return new BookEntity(id, "Test Book", "Author", total, available);
    }

    private UserEntity userEntity(UUID id) {
        return new UserEntity(id, "Alice", "alice", "encoded", UserRole.USER);
    }

    private LoanEntity loanEntity(UUID id, UUID bookId, UUID userId, LoanStatus status) {
        return new LoanEntity(id, bookId, userId, LocalDate.now(), null, status);
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
        BookEntity entity = bookEntity(BOOK_ID, 5, 5);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(entity));
        Book found = libraryService.getBook(BOOK_ID.toString());
        assertEquals(BOOK_ID.toString(), found.getId());
    }

    @Test
    void getBook_shouldThrowWhenNotFound() {
        when(bookRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> libraryService.getBook(UUID.randomUUID().toString()));
    }

    @Test
    void getAllBooks_shouldReturnAllAddedBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                bookEntity(UUID.randomUUID(), 5, 5),
                bookEntity(UUID.randomUUID(), 3, 3)
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
        UserEntity entity = userEntity(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(entity));
        User found = libraryService.getUser(USER_ID.toString());
        assertEquals(USER_ID.toString(), found.getId());
    }

    @Test
    void getUser_shouldThrowWhenNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> libraryService.getUser(UUID.randomUUID().toString()));
    }

    @Test
    void loanBook_shouldCreateActiveLoan() {
        BookEntity book = bookEntity(BOOK_ID, 5, 5);
        UserEntity user = userEntity(USER_ID);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanRepository.countByUserIdAndStatus(USER_ID, LoanStatus.ACTIVE)).thenReturn(0L);
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan loan = libraryService.loanBook(BOOK_ID.toString(), USER_ID.toString());
        assertNotNull(loan.getId());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(BOOK_ID.toString(), loan.getBookId());
    }

    @Test
    void loanBook_shouldThrowWhenBookNotAvailable() {
        BookEntity book = bookEntity(BOOK_ID, 1, 0);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        assertThrows(BookNotAvailableException.class,
                () -> libraryService.loanBook(BOOK_ID.toString(), USER_ID.toString()));
    }

    @Test
    void loanBook_shouldThrowWhenLoanLimitExceeded() {
        BookEntity book = bookEntity(BOOK_ID, 5, 5);
        UserEntity user = userEntity(USER_ID);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanRepository.countByUserIdAndStatus(USER_ID, LoanStatus.ACTIVE)).thenReturn(3L);
        assertThrows(LoanLimitExceededException.class,
                () -> libraryService.loanBook(BOOK_ID.toString(), USER_ID.toString()));
    }

    @Test
    void returnBook_shouldMarkLoanReturnedAndMakeBookAvailable() {
        LoanEntity loan = loanEntity(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE);
        BookEntity book = bookEntity(BOOK_ID, 5, 4);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan returned = libraryService.returnBook(LOAN_ID.toString());
        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
    }

    @Test
    void getLoansByUser_shouldReturnOnlyUserLoans() {
        UserEntity user = userEntity(USER_ID);
        LoanEntity loan = loanEntity(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanRepository.findByUserId(USER_ID)).thenReturn(List.of(loan));

        List<Loan> loans = libraryService.getLoansByUser(USER_ID.toString());
        assertEquals(1, loans.size());
        assertEquals(BOOK_ID.toString(), loans.get(0).getBookId());
    }

    @Test
    void getAllLoans_shouldReturnAllLoans() {
        when(loanRepository.findAll()).thenReturn(List.of(
                loanEntity(UUID.randomUUID(), BOOK_ID, USER_ID, LoanStatus.ACTIVE),
                loanEntity(UUID.randomUUID(), BOOK_ID, USER_ID, LoanStatus.ACTIVE)
        ));
        assertEquals(2, libraryService.getAllLoans().size());
    }

    @Test
    void getLoanById_shouldReturnLoan() {
        LoanEntity entity = loanEntity(LOAN_ID, BOOK_ID, USER_ID, LoanStatus.ACTIVE);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(entity));
        Loan found = libraryService.getLoanById(LOAN_ID.toString());
        assertEquals(LOAN_ID.toString(), found.getId());
    }

    @Test
    void getLoanById_shouldThrowWhenNotFound() {
        when(loanRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> libraryService.getLoanById(UUID.randomUUID().toString()));
    }

    @Test
    void getAllUsers_shouldReturnAllRegisteredUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                userEntity(UUID.randomUUID()),
                userEntity(UUID.randomUUID())
        ));
        assertEquals(2, libraryService.getAllUsers().size());
    }
}
