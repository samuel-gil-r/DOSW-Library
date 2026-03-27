package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;

import java.util.UUID;

public class UserEntityMapper {

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId().toString(),
                entity.getName(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole().name()
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                UUID.fromString(user.getId()),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                edu.eci.dosw.tdd.core.model.UserRole.valueOf(user.getRole())
        );
    }
}
