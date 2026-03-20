package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    MockMvc mockMvc;

    @Mock
    LibraryService libraryService;

    @InjectMocks
    BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void addBook_returns201() throws Exception {
        Book book = new Book("1", "Clean Code", "Robert C. Martin", true);
        when(libraryService.addBook(null, "Clean Code", "Robert C. Martin")).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void getAllBooks_returns200() throws Exception {
        when(libraryService.getAllBooks()).thenReturn(List.of(
                new Book("1", "Clean Code", "Martin", true),
                new Book("2", "Refactoring", "Fowler", true)
        ));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBook_returns200() throws Exception {
        Book book = new Book("1", "Clean Code", "Martin", true);
        when(libraryService.getBook("1")).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getBook_notFound_returns404() throws Exception {
        when(libraryService.getBook(anyString()))
                .thenThrow(new BookNotFoundException("Book not found: xyz"));

        mockMvc.perform(get("/api/books/xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found: xyz"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
