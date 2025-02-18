package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@JdbcTest
@AutoConfigureTestDatabase
// @RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    // public static final int TEST_USER_ID = 1;

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    void getUserById() {
        Optional<User> userOptional = userDbStorage.getUserById(1);

        System.out.println("-----" + userDbStorage.getDbInfo());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

}