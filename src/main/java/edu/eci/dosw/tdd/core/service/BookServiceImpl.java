package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final Map<String, Book> books = new HashMap<>();

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        String id = UUID.randomUUID().toString();
        Book book = new Book(id, bookDTO.getTitle(), bookDTO.getAuthor(), true);
        books.put(id, book);
        return toDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return books.values().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(String id) {
        return toDTO(findBook(id));
    }

    @Override
    public BookDTO updateAvailability(String id, boolean available) {
        Book book = findBook(id);
        book.setAvailable(available);
        return toDTO(book);
    }

    Book findBook(String id) {
        Book book = books.get(id);
        if (book == null) {
            throw new BookNotFoundException("Book not found: " + id);
        }
        return book;
    }

    private BookDTO toDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }
}
