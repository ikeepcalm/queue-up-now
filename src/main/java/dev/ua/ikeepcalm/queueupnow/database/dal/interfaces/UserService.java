package dev.ua.ikeepcalm.queue.database.dal.interfaces;

import dev.ua.ikeepcalm.queue.database.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.queue.database.entities.reverence.ReverenceUser;
import dev.ua.ikeepcalm.queue.database.exceptions.NoSuchEntityException;

import java.util.List;

public interface UserService {
    ReverenceUser findById(long userId, ReverenceChat chat) throws NoSuchEntityException;

    void updateAll();

    ReverenceUser findByUsername(String var1, ReverenceChat var2);

    List<ReverenceUser> findAll(ReverenceChat var1);

    void save(ReverenceUser var1);

    void delete(ReverenceUser var1);

    boolean checkIfUserExists(long var1, ReverenceChat var3);

    boolean checkIfMentionedUserExists(String var1, ReverenceChat var2);
}
