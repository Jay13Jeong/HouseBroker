package com.jjeong.kiwi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Getter
    @Setter
    private Date timestamp;

    @Getter
    @Setter
    private String senderIp;

    public Chat() {
        this.timestamp = new Date();
    }
}

