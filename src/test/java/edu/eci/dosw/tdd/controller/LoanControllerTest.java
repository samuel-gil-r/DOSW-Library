package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    MockMvc mockMvc;

    @Mock
    LibraryService libraryService;

    @InjectMocks
    LoanController loanController;

    private final Book book = new Book("book-1", "Clean Code", "Martin", true);
    private final User user = new User("user-1", "Alice");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(loanController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void loanBook_returns201() throws Exception {
        Loan loan = new Loan("loan-1", book, user, LocalDate.now(), null, "ACTIVE");
        when(libraryService.loanBook("book-1", "user-1")).thenReturn(loan);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":\"book-1\",\"userId\":\"user-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void loanBook_bookNotAvailable_returns404() throws Exception {
        when(libraryService.loanBook(anyString(), anyString()))
                .thenThrow(new BookNotAvailableException("Book is not available: book-1"));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":\"book-1\",\"userId\":\"user-1\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book is not available: book-1"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void loanBook_limitExceeded_returns422() throws Exception {
        when(libraryService.loanBook(anyString(), anyString()))
                .thenThrow(new LoanLimitExceededException("Loan limit exceeded for user: user-1"));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":\"book-1\",\"userId\":\"user-1\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Loan limit exceeded for user: user-1"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void getAllLoans_returns200() throws Exception {
        Loan loan = new Loan("loan-1", book, user, LocalDate.now(), null, "ACTIVE");
        when(libraryService.getAllLoans()).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLoanById_returns200() throws Exception {
        Loan loan = new Loan("loan-1", book, user, LocalDate.now(), null, "ACTIVE");
        when(libraryService.getLoanById("loan-1")).thenReturn(loan);

        mockMvc.perform(get("/api/loans/loan-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("loan-1"));
    }

    @Test
    void returnBook_returns200() throws Exception {
        Loan loan = new Loan("loan-1", book, user, LocalDate.now(), LocalDate.now(), "RETURNED");
        when(libraryService.returnBook("loan-1")).thenReturn(loan);

        mockMvc.perform(put("/api/loans/loan-1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void getLoansByUser_returns200() throws Exception {
        Loan loan = new Loan("loan-1", book, user, LocalDate.now(), null, "ACTIVE");
        when(libraryService.getLoansByUser("user-1")).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/loans/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
