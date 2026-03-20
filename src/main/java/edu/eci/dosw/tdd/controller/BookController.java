package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        log.info("POST /api/books - addBook: {}", bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(bookDTO));
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("GET /api/books - getAllBooks");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        log.info("GET /api/books/{} - getBookById", id);
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<BookDTO> updateAvailability(@PathVariable String id,
                                                      @RequestParam boolean available) {
        log.info("PATCH /api/books/{}/availability - updateAvailability: {}", id, available);
        return ResponseEntity.ok(bookService.updateAvailability(id, available));
    }
}
