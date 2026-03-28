package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
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
public class UserRepositoryMongoImpl implements UserRepositoryPort {

    private final MongoUserRepository mongoUserRepository;

    @Override
    public User save(User user) {
        UserDocument doc = mongoUserRepository.findById(user.getId())
                .orElse(new UserDocument());
        doc.setId(user.getId());
        doc.setName(user.getName());
        doc.setUsername(user.getUsername());
        doc.setPassword(user.getPassword());
        doc.setRole(user.getRole());
        if (doc.getAddedAt() == null) {
            doc.setAddedAt(LocalDate.now());
        }
        return toDomain(mongoUserRepository.save(doc));
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return mongoUserRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        mongoUserRepository.deleteById(id);
    }

    private User toDomain(UserDocument doc) {
        return new User(doc.getId(), doc.getName(), doc.getUsername(), doc.getPassword(), doc.getRole());
    }
}
