package com.jjeong.kiwi.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Message() {
        this.timestamp = LocalDateTime.now(); // 현재 시간으로 초기화
    }


//    private String sender;
//    private String receiver;
//    private String message;
//    private String date;
//    private String status;

}
