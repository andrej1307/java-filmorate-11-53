package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();
    private Integer userId = 0;

    @Override
    public User addNewUser(User user) {
        userId++;
        user.setId(userId);
        users.put(userId, user);
        friends.put(userId, new HashSet<>());
        return user;
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public void updateUser(User updUser) {
        users.put(updUser.getId(), updUser);
    }

    @Override
    public void removeAllUsers() {
        friends.clear();
        users.clear();
        userId = 0;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    @Override
    public void breakUpFriends(Integer id1, Integer id2) {
        friends.get(id1).remove(id2);
        friends.get(id2).remove(id1);
    }

    @Override
    public Collection<User> getUserFriends(Integer userId) {
        List<User> dtoFriends = new ArrayList<>();
        for (Integer friendId : friends.get(userId)) {
            dtoFriends.add(users.get(friendId));
        }
        return dtoFriends;
    }

    @Override
    public Collection<User> getCommonFriends(Integer id1, Integer id2) {
        List<Integer> friendsId = new ArrayList<>(friends.get(id1));
        friendsId.retainAll(new ArrayList<>(friends.get(id2)));
        List<User> dtoFriends = new ArrayList<>();
        for (Integer id : friendsId) {
            dtoFriends.add(users.get(id));
        }
        return dtoFriends;
    }

    public String getDbInfo() {
        return "*";
    }
}
