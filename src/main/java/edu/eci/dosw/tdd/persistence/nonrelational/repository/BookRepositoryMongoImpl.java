package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookAvailability;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("mongo")
@RequiredArgsConstructor
public class BookRepositoryMongoImpl implements BookRepositoryPort {

    private final MongoBookRepository mongoBookRepository;

    @Override
    public Book save(Book book) {
        BookDocument doc = mongoBookRepository.findById(book.getId())
                .orElse(new BookDocument());
        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());
        if (doc.getAvailability() == null) {
            doc.setAvailability(new BookAvailability());
        }
        doc.getAvailability().setTotalCopies(book.getTotalStock());
        doc.getAvailability().setAvailableCopies(book.getAvailableStock());
        doc.getAvailability().setBorrowedCopies(book.getTotalStock() - book.getAvailableStock());
        if (doc.getAddedAt() == null) {
            doc.setAddedAt(LocalDate.now());
        }
        return toDomain(mongoBookRepository.save(doc));
    }

    @Override
    public Optional<Book> findById(String id) {
        return mongoBookRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return mongoBookRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        mongoBookRepository.deleteById(id);
    }

    private Book toDomain(BookDocument doc) {
        int total = doc.getAvailability() != null ? doc.getAvailability().getTotalCopies() : 0;
        int available = doc.getAvailability() != null ? doc.getAvailability().getAvailableCopies() : 0;
        return new Book(doc.getId(), doc.getTitle(), doc.getAuthor(), total, available);
    }
}
