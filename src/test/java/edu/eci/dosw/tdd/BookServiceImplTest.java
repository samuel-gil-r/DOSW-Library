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
}
