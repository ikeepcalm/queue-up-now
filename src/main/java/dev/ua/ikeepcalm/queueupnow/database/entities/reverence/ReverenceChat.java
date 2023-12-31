package dev.ua.ikeepcalm.queueupnow.database.entities.reverence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reverence_chats")
public class ReverenceChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long chatId;

    @OneToMany(mappedBy = "channel")
    private Set<ReverenceUser> users;

}

