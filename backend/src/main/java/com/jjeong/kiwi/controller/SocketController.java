package com.jjeong.kiwi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/hello")
    @SendTo("/topic/hi")
    public String sendChatMessage(String message) {
        return message;
    }

    @MessageMapping("/send/{socketId}")
    public void sendMessageToClient(
            @DestinationVariable String socketId,
            String message) {
        messagingTemplate.convertAndSendToUser(socketId, "/topic/message", message);
    }
}
