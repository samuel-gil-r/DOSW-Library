package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;

import java.util.UUID;

public class BookEntityMapper {

    public static Book toDomain(BookEntity entity) {
        return new Book(
                entity.getId().toString(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getTotalStock(),
                entity.getAvailableStock()
        );
    }

    public static BookEntity toEntity(Book book) {
        return new BookEntity(
                UUID.fromString(book.getId()),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalStock(),
                book.getAvailableStock()
        );
    }
}
