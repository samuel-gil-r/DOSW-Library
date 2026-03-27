package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookRequestDTO;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Books", description = "Book management endpoints")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final LibraryService libraryService;

    @Operation(summary = "Add a new book")
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Book> addBook(@RequestBody BookRequestDTO body) {
        Book book = libraryService.addBook(body.getId(), body.getTitle(), body.getAuthor(), body.getTotalStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @Operation(summary = "Get a book by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return ResponseEntity.ok(libraryService.getBook(id));
    }
}
