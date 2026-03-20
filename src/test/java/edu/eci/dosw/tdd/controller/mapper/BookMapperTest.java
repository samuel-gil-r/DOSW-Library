package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper mapper = new BookMapper();

    @Test
    void toDTO_returnsNullWhenBookIsNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_mapsAllFields() {
        Book book = new Book("1", "Clean Code", "Martin", true);
        BookDTO dto = mapper.toDTO(book);
        assertEquals("1", dto.getId());
        assertEquals("Clean Code", dto.getTitle());
        assertEquals("Martin", dto.getAuthor());
    }

    @Test
    void toModel_returnsNullWhenDtoIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_mapsAllFields() {
        BookDTO dto = new BookDTO("1", "Clean Code", "Martin");
        Book book = mapper.toModel(dto);
        assertEquals("1", book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Martin", book.getAuthor());
    }
}
