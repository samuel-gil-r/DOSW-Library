package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {

    @Id
    private String id;
    private String title;
    private String author;
    private String isbn;
    private PublicationType publicationType;
    private LocalDate publicationDate;
    private List<String> categories;
    private BookMetadata metadata;
    private BookAvailability availability;
    private LocalDate addedAt;
}
