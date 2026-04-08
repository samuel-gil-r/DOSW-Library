package edu.eci.dosw.tdd;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepositoryPort loanRepository;

    @Mock
    private BookRepositoryPort bookRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void dadoQueTengo1ReservaRegistrada_cuandoLoConsultoANivelDeServicio_entoncesLaConsultaSeraExitosaValidandoElCampoId() {
        String loanId = "loan-123";
        Loan loan = new Loan(loanId, "book-1", "user-1", LocalDate.now(), null, LoanStatus.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        Loan result = libraryService.getLoanById(loanId);

        assertNotNull(result);
        assertEquals(loanId, result.getId());
    }

    @Test
    void dadoQueNoHayNingunaReservaRegistrada_cuandoLaConsulto_entoncesNoRetornaNingunResultado() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> result = libraryService.getAllLoans();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void dadoQueNoHayNingunaReservaRegistrada_cuandoLaCreo_entoncesLaCreacionSeraExitosa() {
        String bookId = "book-1";
        String userId = "user-1";
        Book book = new Book(bookId, "Effective Java", "Joshua Bloch", 5, 5);
        User user = new User(userId, "Alice", "alice", "encoded", "USER");
        Loan savedLoan = new Loan("loan-1", bookId, userId, LocalDate.now(), null, LoanStatus.ACTIVE);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanRepository.countActiveByUserId(userId)).thenReturn(0L);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        Loan result = libraryService.loanBook(bookId, userId);

        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals(userId, result.getUserId());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
    }

    @Test
    void dadoQueTengo1ReservaRegistrada_cuandoLaElimino_entoncesLaEliminacionSeraExitosa() {
        String loanId = "loan-123";
        Loan loan = new Loan(loanId, "book-1", "user-1", LocalDate.now(), null, LoanStatus.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertDoesNotThrow(() -> libraryService.deleteLoan(loanId));

        verify(loanRepository).delete(loanId);
    }

    @Test
    void dadoQueTengo1ReservaRegistrada_cuandoLaEliminoYConsulto_entoncesNoRetornaNingunResultado() {
        String loanId = "loan-123";
        Loan loan = new Loan(loanId, "book-1", "user-1", LocalDate.now(), null, LoanStatus.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        libraryService.deleteLoan(loanId);
        List<Loan> result = libraryService.getAllLoans();

        assertTrue(result.isEmpty());
        verify(loanRepository).delete(loanId);
    }
}
