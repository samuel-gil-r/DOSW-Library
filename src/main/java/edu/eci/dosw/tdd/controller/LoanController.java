package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanRequestDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Loans", description = "Loan management endpoints")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LibraryService libraryService;

    @Operation(summary = "Create a new loan")
    @PostMapping
    public ResponseEntity<Loan> loanBook(@RequestBody LoanRequestDTO body) {
        Loan loan = libraryService.loanBook(body.getBookId(), body.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @Operation(summary = "Get all loans")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(libraryService.getAllLoans());
    }

    @Operation(summary = "Get a loan by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Loan> getLoanById(@PathVariable String id) {
        return ResponseEntity.ok(libraryService.getLoanById(id));
    }

    @Operation(summary = "Return a book (close a loan)")
    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable String id) {
        return ResponseEntity.ok(libraryService.returnBook(id));
    }

    @Operation(summary = "Get all loans for a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable String userId) {
        return ResponseEntity.ok(libraryService.getLoansByUser(userId));
    }
}
