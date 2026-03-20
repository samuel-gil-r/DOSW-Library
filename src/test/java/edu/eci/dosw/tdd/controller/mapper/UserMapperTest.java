package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toDTO_returnsNullWhenUserIsNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_mapsAllFields() {
        User user = new User("1", "Alice");
        UserDTO dto = mapper.toDTO(user);
        assertEquals("1", dto.getId());
        assertEquals("Alice", dto.getName());
    }

    @Test
    void toModel_returnsNullWhenDtoIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_mapsAllFields() {
        UserDTO dto = new UserDTO("1", "Alice");
        User user = mapper.toModel(dto);
        assertEquals("1", user.getId());
        assertEquals("Alice", user.getName());
    }
}
