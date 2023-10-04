package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketService socketService;
    private final UserService userService;

    @MessageMapping("/hello")
    @SendTo("/topic/hi")
    public String sendChatMessage() {
        return "";
    }

    @MessageMapping("/send/{id}")
    public void sendMessageToClient(
            Principal principal,
            @DestinationVariable Long id,
            String message) {
        System.out.println("*********** i am 3");
        System.out.println("*********** principal " + principal.getName());
        System.out.println("*********** msg " + message);
        for (String target : socketService.getSocketSetByUserPk(id)){
            messagingTemplate.convertAndSendToUser(target, "/topic/message", message);
        }
        System.out.println("*********** i am 3 end");
    }

    @MessageMapping("/logout")
    public void sendMessageToClient(Principal principal, SimpMessageHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        String socketId = attributes.get("socketId").toString();
        for (String target : socketService.getSocketSetByUserPk(Long.valueOf(principal.getName()))){
            messagingTemplate.convertAndSendToUser(target, "/topic/refresh", "");
        }
        //이 소켓과 관련있는 소켓들 모두 끊기...
    }
}
