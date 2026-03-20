package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
    }

    @Test
    void addBook_shouldReturnBookWithId() {
        Book book = libraryService.addBook(null, "Clean Code", "Robert C. Martin");
        assertNotNull(book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void getBook_shouldReturnBookById() {
        Book added = libraryService.addBook(null, "Refactoring", "Martin Fowler");
        Book found = libraryService.getBook(added.getId());
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void getBook_shouldThrowWhenNotFound() {
        assertThrows(BookNotFoundException.class, () -> libraryService.getBook("nonexistent"));
    }

    @Test
    void getAllBooks_shouldReturnAllAddedBooks() {
        libraryService.addBook(null, "Book A", "Author A");
        libraryService.addBook(null, "Book B", "Author B");
        List<Book> books = libraryService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void registerUser_shouldReturnUserWithId() {
        User user = libraryService.registerUser(null, "Alice");
        assertNotNull(user.getId());
        assertEquals("Alice", user.getName());
    }

    @Test
    void getUser_shouldReturnUserById() {
        User added = libraryService.registerUser(null, "Bob");
        User found = libraryService.getUser(added.getId());
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void getUser_shouldThrowWhenNotFound() {
        assertThrows(UserNotFoundException.class, () -> libraryService.getUser("nonexistent"));
    }

    @Test
    void loanBook_shouldCreateActiveLoan() {
        Book book = libraryService.addBook(null, "The Pragmatic Programmer", "Andy Hunt");
        User user = libraryService.registerUser(null, "Carol");
        Loan loan = libraryService.loanBook(book.getId(), user.getId());
        assertNotNull(loan.getId());
        assertEquals("ACTIVE", loan.getStatus());
        assertFalse(libraryService.getBook(book.getId()).isAvailable());
        assertEquals(1, libraryService.getLoansByUser(user.getId()).size());
    }

    @Test
    void loanBook_shouldThrowWhenBookNotAvailable() {
        Book book = libraryService.addBook(null, "Effective Java", "Joshua Bloch");
        User user1 = libraryService.registerUser(null, "Dave");
        User user2 = libraryService.registerUser(null, "Eve");
        libraryService.loanBook(book.getId(), user1.getId());
        assertThrows(BookNotAvailableException.class, () -> libraryService.loanBook(book.getId(), user2.getId()));
    }

    @Test
    void loanBook_shouldThrowWhenLoanLimitExceeded() {
        User user = libraryService.registerUser(null, "Frank");
        for (int i = 0; i < 3; i++) {
            Book b = libraryService.addBook(null, "Book " + i, "Author " + i);
            libraryService.loanBook(b.getId(), user.getId());
        }
        Book extra = libraryService.addBook(null, "Extra Book", "Extra Author");
        assertThrows(LoanLimitExceededException.class, () -> libraryService.loanBook(extra.getId(), user.getId()));
    }

    @Test
    void returnBook_shouldMarkLoanReturnedAndMakeBookAvailable() {
        Book book = libraryService.addBook(null, "Domain-Driven Design", "Eric Evans");
        User user = libraryService.registerUser(null, "Grace");
        Loan loan = libraryService.loanBook(book.getId(), user.getId());
        Loan returned = libraryService.returnBook(loan.getId());
        assertEquals("RETURNED", returned.getStatus());
        assertNotNull(returned.getReturnDate());
        assertTrue(libraryService.getBook(book.getId()).isAvailable());
        assertEquals(0, libraryService.getLoansByUser(user.getId()).stream()
                .filter(l -> "ACTIVE".equals(l.getStatus())).count());
    }

    @Test
    void getLoansByUser_shouldReturnOnlyUserLoans() {
        Book book1 = libraryService.addBook(null, "Book 1", "Author 1");
        Book book2 = libraryService.addBook(null, "Book 2", "Author 2");
        User user1 = libraryService.registerUser(null, "Heidi");
        User user2 = libraryService.registerUser(null, "Ivan");
        libraryService.loanBook(book1.getId(), user1.getId());
        libraryService.loanBook(book2.getId(), user2.getId());
        List<Loan> loans = libraryService.getLoansByUser(user1.getId());
        assertEquals(1, loans.size());
        assertEquals(book1.getId(), loans.get(0).getBook().getId());
    }

    @Test
    void getAllLoans_shouldReturnAllLoans() {
        Book book1 = libraryService.addBook(null, "Book 1", "Author 1");
        Book book2 = libraryService.addBook(null, "Book 2", "Author 2");
        User user = libraryService.registerUser(null, "Jack");
        libraryService.loanBook(book1.getId(), user.getId());
        libraryService.loanBook(book2.getId(), user.getId());
        assertEquals(2, libraryService.getAllLoans().size());
    }

    @Test
    void getLoanById_shouldReturnLoan() {
        Book book = libraryService.addBook(null, "Book X", "Author X");
        User user = libraryService.registerUser(null, "Kate");
        Loan loan = libraryService.loanBook(book.getId(), user.getId());
        Loan found = libraryService.getLoanById(loan.getId());
        assertEquals(loan.getId(), found.getId());
    }

    @Test
    void getLoanById_shouldThrowWhenNotFound() {
        assertThrows(RuntimeException.class, () -> libraryService.getLoanById("nonexistent"));
    }

    @Test
    void getAllUsers_shouldReturnAllRegisteredUsers() {
        libraryService.registerUser(null, "Leo");
        libraryService.registerUser(null, "Mia");
        assertEquals(2, libraryService.getAllUsers().size());
    }
}
