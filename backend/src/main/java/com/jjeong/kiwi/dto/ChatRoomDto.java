package com.jjeong.kiwi.dto;

import com.jjeong.kiwi.dto.ChatDto;
import com.jjeong.kiwi.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ChatRoomDto {
    private Long id;
    private List<ChatDto> chats;
    private String roomName;
    private Set<User> users;
    private Date timestamp;
}
