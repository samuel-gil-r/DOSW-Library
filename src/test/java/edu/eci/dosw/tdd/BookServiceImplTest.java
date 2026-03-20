package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceImplTest {

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl();
    }

    @Test
    void testAddBook_success() {
        BookDTO input = new BookDTO(null, "Clean Code", "Robert C. Martin");
        BookDTO result = bookService.addBook(input);

        assertNotNull(result.getId());
        assertEquals("Clean Code", result.getTitle());
        assertEquals("Robert C. Martin", result.getAuthor());
    }

    @Test
    void testGetBookById_notFound() {
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById("nonexistent-id"));
    }

    @Test
    void testGetAllBooks_returnsAll() {
        bookService.addBook(new BookDTO(null, "Book A", "Author A"));
        bookService.addBook(new BookDTO(null, "Book B", "Author B"));
        assertEquals(2, bookService.getAllBooks().size());
    }

    @Test
    void testUpdateAvailability_success() {
        BookDTO added = bookService.addBook(new BookDTO(null, "Clean Code", "Martin"));
        BookDTO updated = bookService.updateAvailability(added.getId(), false);
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void testGetBookById_success() {
        BookDTO added = bookService.addBook(new BookDTO(null, "Refactoring", "Fowler"));
        BookDTO found = bookService.getBookById(added.getId());
        assertEquals(added.getId(), found.getId());
        assertEquals("Refactoring", found.getTitle());
    }
}
