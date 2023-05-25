package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.Message;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Setter
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message") // "/app/message"로 오면 받는다.
    @SendTo("/chatroom/public")
    private Message receivePublicMessage(@Payload Message message){
        System.out.println(message);
        return  message;
    }

    @MessageMapping("/private-message")
    private Message receivePrivateMessage(@Payload Message message){

        // 변환한 내용을 "/user/targetName/private"로 보낸다
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private",message);
        return  message;
    }
}
