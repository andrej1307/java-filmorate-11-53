package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
    private static final String SQL_INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
    private static final String SQL_UPDATE_USER = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday WHERE id = :id";
    private static final String SQL_FIND_USER = "SELECT * FROM users WHERE id = :id";
    private static final String SQL_FIND_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_DELETE_USERS = "DELETE FROM users WHERE id <> :id";
    private static final String SQL_ADD_FRIEND = "MERGE INTO friends (user_id, friend_id, confirmed) VALUES (:userId, :friendId, FALSE)";
    private static final String SQL_REMOVE_FRIEND = "DELETE FROM friends WHERE (user_id = :userId) AND (friend_id = :friendId)";


    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public UserDbStorage(NamedParameterJdbcTemplate jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    /**
     * Добавление в базу нового пользователя
     *
     * @param newUser - объект для добавления
     * @return - подтвержденный объект
     */
    @Override
    public User addNewUser(User newUser) {
        // для доступа к сгенерированому ключу новой записи создаем объект GeneratedKeyHolder
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbc.update(SQL_INSERT_USER,
                new MapSqlParameterSource()
                        .addValue("email", newUser.getEmail())
                        .addValue("login", newUser.getLogin())
                        .addValue("name", newUser.getName())
                        .addValue("birthday", newUser.getBirthday(), Types.DATE),
                generatedKeyHolder
        );

        // присваиваем сгенерирванный ключ записи в качестве идентификатора пользователя
        newUser.setId(generatedKeyHolder.getKey().intValue());
        return newUser;
    }

    /**
     * Поиск пользователя по идентификатору.
     *
     * @param id - идентификатор пользователя
     * @return - Optional<User>
     */
    @Override
    public Optional<User> getUserById(Integer id) {
        try {
            User user = jdbc.queryForObject(SQL_FIND_USER,
                    new MapSqlParameterSource()
                            .addValue("id", id),
                    mapper);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Поиск всех пользователей
     *
     * @return - список пользователей
     */
    @Override
    public Collection<User> findAllUsers() {
        try {
            return jdbc.query(SQL_FIND_ALL_USERS, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    /**
     * Обновление сведений о пользователе
     *
     * @param updUser - объект с обновленной информацией
     */
    @Override
    public void updateUser(User updUser) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", updUser.getEmail());
        params.addValue("login", updUser.getLogin());
        params.addValue("name", updUser.getName());
        params.addValue("birthday", updUser.getBirthday(), Types.DATE);
        params.addValue("id", updUser.getId());

        int rowsUpdated = jdbc.update(SQL_UPDATE_USER, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    /**
     * Удаление всех пользователей
     */
    @Override
    public void removeAllUsers() {
        jdbc.update(SQL_DELETE_USERS, new MapSqlParameterSource()
                .addValue("id", 0)
        );
    }

    /**
     * Добавление "друга"
     *
     * @param userId   - идентификатор пользователя
     * @param friendId - идентификатор друга
     */
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbc.update(SQL_ADD_FRIEND, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId)
        );
    }

    /**
     * Исключение из "друзей"
     *
     * @param userId    - идентификатор пользователя
     * @param friendsId - идентификатор друга
     */
    @Override
    public void breakUpFriends(Integer userId, Integer friendsId) {
        jdbc.update(SQL_REMOVE_FRIEND, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendsId)
        );
    }

    /**
     * Поиск друзей пользователя.
     *
     * @param userId - идентификатор пользователя
     * @return - список друзей
     */
    @Override
    public Collection<User> getUserFriends(Integer userId) {
        String sql = "SELECT * FROM users WHERE id IN " +
                "(SELECT friend_id FROM friends WHERE user_id = :userId)";
        try {
            return jdbc.query(sql, new MapSqlParameterSource()
                            .addValue("userId", userId),
                    mapper
            );
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }

    /**
     * Поиск общих друзей
     *
     * @param id1 - идентификатор первого пользователя
     * @param id2 - идентификатор второго пользователя
     * @return - спмсок общих друзей
     */
    @Override
    public Collection<User> getCommonFriends(Integer id1, Integer id2) {
        String sql = "SELECT * FROM users WHERE id IN (SELECT tu.friend_id " +
                "FROM (SELECT * FROM friends WHERE user_id = :userId1) AS tu " +
                "INNER JOIN (SELECT * FROM friends WHERE user_id = :userId2) AS tf " +
                "WHERE tu.friend_id = tf.friend_id)";
        try {
            return jdbc.query(sql, new MapSqlParameterSource()
                            .addValue("userId1", id1)
                            .addValue("userId2", id2),
                    mapper
            );
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        }
    }
}
