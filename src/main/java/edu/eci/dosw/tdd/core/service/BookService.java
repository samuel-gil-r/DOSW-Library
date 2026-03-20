package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.BookDTO;

import java.util.List;

public interface BookService {
    BookDTO addBook(BookDTO bookDTO);
    List<BookDTO> getAllBooks();
    BookDTO getBookById(String id);
    BookDTO updateAvailability(String id, boolean available);
}
