package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.service.BookServiceImpl;
import edu.eci.dosw.tdd.core.service.LoanServiceImpl;
import edu.eci.dosw.tdd.core.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceImplTest {

    private BookServiceImpl bookService;
    private UserServiceImpl userService;
    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl();
        userService = new UserServiceImpl();
        loanService = new LoanServiceImpl(bookService, userService);
    }

    @Test
    void testCreateLoan_success() {
        BookDTO book = bookService.addBook(new BookDTO(null, "Effective Java", "Joshua Bloch"));
        UserDTO user = userService.registerUser(new UserDTO(null, "Bob"));

        LoanDTO loanDTO = new LoanDTO(book.getId(), user.getId(), null, null, null);
        LoanDTO result = loanService.createLoan(loanDTO);

        assertNotNull(result);
        assertEquals(book.getId(), result.getBookId());
        assertEquals(user.getId(), result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getLoanDate());
    }

    @Test
    void testCreateLoan_bookNotAvailable() {
        BookDTO book = bookService.addBook(new BookDTO(null, "The Pragmatic Programmer", "Andy Hunt"));
        UserDTO user1 = userService.registerUser(new UserDTO(null, "Carol"));
        UserDTO user2 = userService.registerUser(new UserDTO(null, "Dave"));

        loanService.createLoan(new LoanDTO(book.getId(), user1.getId(), null, null, null));

        assertThrows(BookNotAvailableException.class, () ->
                loanService.createLoan(new LoanDTO(book.getId(), user2.getId(), null, null, null)));
    }

    @Test
    void testCreateLoan_loanLimitExceeded() {
        UserDTO user = userService.registerUser(new UserDTO(null, "Eve"));

        for (int i = 0; i < 3; i++) {
            BookDTO book = bookService.addBook(new BookDTO(null, "Book " + i, "Author " + i));
            loanService.createLoan(new LoanDTO(book.getId(), user.getId(), null, null, null));
        }

        BookDTO extraBook = bookService.addBook(new BookDTO(null, "Extra Book", "Author X"));
        assertThrows(LoanLimitExceededException.class, () ->
                loanService.createLoan(new LoanDTO(extraBook.getId(), user.getId(), null, null, null)));
    }
}
