package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryAbstractStorage;

import java.util.*;

@Component
public class InMemoryUserStorage extends InMemoryAbstractStorage<User> implements UserStorage {

    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();


    @Override
    public User addNewUser(User newUser) {
        User user = super.addNew(newUser);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return super.getElement(id);
    }

    @Override
    public Collection<User> findAllUsers() {
        return super.findAll();
    }

    @Override
    public User updateUser(User updUser) {
        return super.update(updUser);
    }

    @Override
    public void removeAllUsers() {
        friends.clear();
        super.clear();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if ((getElement(userId) == null) || (getElement(friendId) == null)) {
            throw new NotFoundException("Не найден один или оба друга. " + userId + "," + friendId);
        }
        friends.get(userId).add(friendId);
    }

    @Override
    public void breakUpFriends(Integer id1, Integer id2) {
        if(getElement(id1) != null) {
            friends.get(id1).remove(id2);
        }

        if(getElement(id2) != null) {
            friends.get(id2).remove(id1);
        }
    }

    @Override
    public List<Integer> findAllFriends(Integer userId) {
        if (!friends.containsKey(userId)) {
            throw new NotFoundException("Не найден id=" + userId);
        }
        return new ArrayList<>(friends.get(userId));
    }
}
