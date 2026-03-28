package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.persistence.mapper.UserEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
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
public class UserRepositoryJpaImpl implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return UserEntityMapper.toDomain(userRepository.save(UserEntityMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntityMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(UserEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
