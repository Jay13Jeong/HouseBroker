package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
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
    public String sendChatMessage(String message, SimpMessageHeaderAccessor accessor) {
        String id = "NOT";
        try {
            Map<String, Object> attributes = accessor.getSessionAttributes();
            String senderSocketId = attributes.get("socketId").toString();
            id = socketService.getUserPkBySocketId(senderSocketId).toString();
        } catch (Exception e){

        }
        return id + message;
    }

    @MessageMapping("/send/{userId}")
    public void sendMessageToClient(
            @DestinationVariable Long userId,
            String message) throws IOException {
        Set<String> socketSet = socketService.getSocketSetByUserPk(userId);

        System.out.println("++++++++++++++++++++++++");
        for (String clientSocketId : socketSet) {
            System.out.println(clientSocketId);
            WebSocketSession session = socketService.getWebSocketSession(clientSocketId);
            session.sendMessage(new TextMessage(message));
//            messagingTemplate.convertAndSendToUser(session, "/topic/message", message);
        }
        System.out.println("++++++++++++++++++++++++");
    }

    @MessageMapping("/send2/{email}")
    public void sendMessageToClient(
            @DestinationVariable String email,
            String message,
            SimpMessageHeaderAccessor accessor) {
        Long userId = userService.getUserByEmail(email).getId();
        Set<String> socketSet = socketService.getSocketSetByUserPk(userId);
        Map<String, Object> attributes = accessor.getSessionAttributes();
        String senderSocketId = attributes.get("socketId").toString();
        for (String clientSocketId : socketSet) {
            messagingTemplate.convertAndSendToUser(clientSocketId, "/topic/message", senderSocketId + message);
        }
    }

    @MessageMapping("/logout")
    public void sendMessageToClient(SimpMessageHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        String socketId = attributes.get("socketId").toString();
        //이 소켓과 관련있는 소켓들 모두 끊기...
    }
}
