package com.jjeong.kiwi.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private String sender;
    private String receiver;
    private String message;
    private String date;
    private String status;

}
