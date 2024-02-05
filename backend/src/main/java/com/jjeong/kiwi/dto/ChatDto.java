package com.jjeong.kiwi.dto;

import com.jjeong.kiwi.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class ChatDto {
    private Long id;
    private String message;
    private ChatRoomDto chatRoom;
    private User sender;
    private User receiver;
    private Date timestamp;
}
