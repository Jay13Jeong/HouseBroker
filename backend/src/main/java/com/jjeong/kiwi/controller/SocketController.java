package com.jjeong.kiwi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/hi")
    public String sendChatMessage(String message) {
        return message;
    }
}
