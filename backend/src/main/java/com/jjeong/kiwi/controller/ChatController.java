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

    @MessageMapping("/hello") // "/app/message"로 오면 받는다.
    @SendTo("/hi")
    public Message receivePublicMessage(@Payload Message message){
        System.out.println(message);
        return  message;
    }

}
