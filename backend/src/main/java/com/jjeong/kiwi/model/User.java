package com.jjeong.kiwi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private int permitLevel;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
        name = "user_chatroom",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "chatroom_id")
    )
    private Set<ChatRoom> chatRooms = new HashSet<>();

    @Getter
    @Setter
    private boolean dormant = false;
}