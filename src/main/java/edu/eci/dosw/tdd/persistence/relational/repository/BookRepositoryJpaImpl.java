package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("relational")
@RequiredArgsConstructor
public class BookRepositoryJpaImpl implements BookRepositoryPort {

    private final BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        BookEntity entity = BookEntityMapper.toEntity(book);
        return BookEntityMapper.toDomain(bookRepository.save(entity));
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(UUID.fromString(id))
                .map(BookEntityMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .map(BookEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        bookRepository.deleteById(UUID.fromString(id));
    }
}
