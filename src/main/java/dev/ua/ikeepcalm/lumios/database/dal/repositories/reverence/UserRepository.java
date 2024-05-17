package dev.ua.ikeepcalm.lumios.database.dal.repositories.reverence;

import dev.ua.ikeepcalm.lumios.database.entities.reverence.ReverenceChat;
import dev.ua.ikeepcalm.lumios.database.entities.reverence.ReverenceUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<ReverenceUser, Long> {
    Optional<ReverenceUser> findReverenceUserByUserIdAndChannel(long userId, ReverenceChat channel);
    List<ReverenceUser> findByUserId(long userId);
    ReverenceUser findReverenceUserByUsernameAndChannel(String username, ReverenceChat channel);
    List<ReverenceUser> findAllByChannel(ReverenceChat channel);
}

