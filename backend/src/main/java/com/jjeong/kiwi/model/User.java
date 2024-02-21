package com.jjeong.kiwi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authid;
    private String username;
    private String email;
    private String password;
    private int permitLevel;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
        name = "user_chatroom",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "chatroom_id")
    )
    private Set<ChatRoom> chatRooms = new HashSet<>();

    private boolean dormant = false;
}