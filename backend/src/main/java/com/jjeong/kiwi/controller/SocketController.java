package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.domain.*;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketService socketService;
    private final UserService userService;
    private final static ObjectMapper objectMapper = new ObjectMapper();
//    private ModelMapper modelMapper;

    @MessageMapping("/hello")
    @SendTo("/topic/hi")
    public String sendChatMessage() {
        return "";
    }

    @MessageMapping("/send/{id}")
    public void sendMessageToClient(
            Principal principal,
            @DestinationVariable Long id,
            String message) throws JsonProcessingException {
//        System.out.println("*********** i am 3");
//        System.out.println("*********** principal " + principal.getName());
//        System.out.println("*********** msg " + message);
        if (!this.checkSender(principal)) return;
        Chat chat = socketService.saveChat(socketService.getUserPkBySocketId(principal.getName()), id, message);
        String chatJson = convertChat2ChatJson(chat);
        for (String target : socketService.getSocketSetByUserPk(id)){
            messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);
        }
//        System.out.println("*********** i am 3 end");
    }

    @MessageMapping("/send/admin")
    public void sendMessageToAdminClient(
            Principal principal,
            String message) throws JsonProcessingException {
        if (!this.checkSender(principal)) return;
        for (String email : userService.getAdminEmails()){
            Long userId = null;
            try {
                userId = userService.getUserByEmail(email).getId();
                if (userId == null) continue;
            }catch (Exception e) {
                continue;
            }
            Long senderId = socketService.getUserPkBySocketId(principal.getName());
            Chat chat = socketService.saveChat(senderId, userId, message);
            String chatJson = convertChat2ChatJson(chat);
            for (String target : socketService.getSocketSetByUserPk(userId)){
                messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);

            }
        }
    }

    private String convertChat2ChatJson(Chat chat) throws JsonProcessingException {
        ChatDto chatDto = new ChatDto();
//        ChatRoomDto chatRoomDto = new ChatRoomDto();
//        chatRoomDto.setId(chat.getChatRoom().getId());
//        chatDto.setChatRoom(chatRoomDto);
        chatDto.setId(chat.getId());
        chatDto.setMessage(chat.getMessage());
        User sender = chat.getSender();
        User recver = chat.getReceiver();
        if (sender != null) sender.setChatRooms(null);
        if (recver != null) recver.setChatRooms(null);
        chatDto.setSender(sender);
        chatDto.setReceiver(recver);
        chatDto.setTimestamp(chat.getTimestamp());

        return objectMapper.writeValueAsString(chatDto);
    }

    private boolean checkSender(Principal principal) throws JsonProcessingException {
        if (socketService.getUserPkBySocketId(principal.getName()) == null){
            ChatDto chatDto = new ChatDto();
            chatDto.setMessage("로그인 후 가능합니다.");
            String chatJson = objectMapper.writeValueAsString(chatDto);
            messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/message", chatJson);
            return false;
        }
        return true;
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<List<Chat>> getChatsByUserId(HttpServletRequest request, @PathVariable Long id) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<List<Chat>>(socketService.getChats(myId, id), HttpStatus.OK);
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(HttpServletRequest request) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<List<ChatRoom>>(socketService.getChatRooms(myId), HttpStatus.OK);
    }

    @GetMapping("/chat/admin")
    public ResponseEntity<List<Chat>> getChatsFromAdmin(HttpServletRequest request) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Chat> chatList = new ArrayList<>();
        for (String email : userService.getAdminEmails()){
            Long userId = null;
            try {
                userId = userService.getUserByEmail(email).getId();
                if (userId == null) continue;
            }catch (Exception e) {
                continue;
            }
            chatList.addAll(socketService.getChats(myId, userId));
        }
        return new ResponseEntity<List<Chat>>(chatList, HttpStatus.OK);
    }


//    @MessageMapping("/logout")
//    public void sendLogoutToClient(Principal principal, SimpMessageHeaderAccessor accessor) {
////        Map<String, Object> attributes = accessor.getSessionAttributes();
////        String socketId = attributes.get("socketId").toString();
////        System.out.println("*********** sendLogoutToClient");
////        System.out.println("*********** principal :" + principal.getName());
////        System.out.println("*********** :" + socketService.getUserPkBySocketId(principal.getName()));
////        System.out.println("*********** sendLogoutToClient end ");
//        Long userId = socketService.getUserPkBySocketId(principal.getName());
//        for (String target : socketService.getSocketSetByUserPk(userId)){
//            messagingTemplate.convertAndSendToUser(target, "/topic/refresh", "");
//        }
//        //이 소켓과 관련있는 소켓들 모두 끊기...
//    }

//    @MessageMapping("/send2/{id}")
//    public void sendMessageTest(
//            Principal principal,
//            @DestinationVariable Long id,
//            String message) throws JsonProcessingException {
////        System.out.println("*********** i am 3");
////        System.out.println("*********** principal " + principal.getName());
////        System.out.println("*********** msg " + message);
//        for (String target : socketService.getSocketSetByUserPk(id)){
//            messagingTemplate.convertAndSendToUser(target, "/topic/message3", message);
//        }
////        System.out.println("*********** i am 3 end");
//    }

}
