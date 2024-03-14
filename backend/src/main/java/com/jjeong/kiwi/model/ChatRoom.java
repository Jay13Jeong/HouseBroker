package com.jjeong.kiwi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chats = new ArrayList<>();

    @Getter
    @Setter
    private String roomName;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(
        name = "chatroom_user",
        joinColumns = @JoinColumn(name = "chatroom_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        this.users.add(user);
    }

    public void addChat(Chat chat) {
        this.chats.add(chat);
    }

    private Date timestamp;

    public ChatRoom() {
        this.timestamp = new Date();
    }

    public boolean haveUser(User user) {
        return this.users.contains(user);
    }
}
