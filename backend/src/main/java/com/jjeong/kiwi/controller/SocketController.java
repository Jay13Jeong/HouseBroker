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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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

//    @MessageMapping("/send/{id}")
//    public void sendMessageToClient(
//            Principal principal,
//            @DestinationVariable Long id,
//            String message) throws JsonProcessingException {
////        System.out.println("*********** i am 3");
////        System.out.println("*********** principal " + principal.getName());
////        System.out.println("*********** msg " + message);
//        if (!this.checkSender(principal)) return;
//        Chat chat = socketService.saveChat(socketService.getUserPkBySocketId(principal.getName()), id, message);
//        String chatJson = convertChat2ChatJson(chat);
//        for (String target : socketService.getSocketSetByUserPk(id)){
//            messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);
//        }
////        System.out.println("*********** i am 3 end");
//    }

    @MessageMapping("/send/room/{roomId}")
    public void sendMessageToClient(
            Principal principal,
            @DestinationVariable Long roomId,
            String message,
            MessageHeaders headers) throws JsonProcessingException {
        if (!this.checkSender(principal)) return;
        String clientIp = this.getClientIp(principal.getName());
        ChatRoom chatRoom = socketService.loadChatRoomById(roomId);
        if (chatRoom == null) {
//            System.out.println("======== sendMessageToClient null");
            return ;
        }
        Long senderId = socketService.getUserPkBySocketId(principal.getName());
        boolean sent = false;
        Chat chat = null;
        //방에 참여중인 나를 제외한 모두에게 보내기.
        for (User recver : chatRoom.getUsers()){
            if (recver.getId() == senderId) continue;
            if (sent == false) {
                chat = socketService.saveChat(senderId, recver.getId(), message, chatRoom, clientIp);
                sent = true;
            }
            Set<String> socketSet = socketService.getSocketSetByUserPk(recver.getId());
            if (socketSet == null) continue;
            chat.setReceiver(recver);
            String chatJson = convertChat2ChatJson(chat);
            for (String target : socketSet){
                messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);
            }
        }
//        System.out.println("*********** i am 3 end");
    }

    private String getClientIp(String socketId){
        return socketService.getIpBySocketId(socketId);
    }

    @MessageMapping("/send/admin")
    public void sendMessageToAdminClient(
            Principal principal,
            String message,
            MessageHeaders headers) throws JsonProcessingException {
        if (!this.checkSender(principal)) return;
//        System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 1");
        System.out.println(headers.toString());
        String clientIp = this.getClientIp(principal.getName());
//        System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 2");
        Long senderId = socketService.getUserPkBySocketId(principal.getName());
//        System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 3");
        Chat chat = null;
        boolean sent = false;
        for (String email : userService.getAdminEmails()){
//            System.out.println("==============sendMessageToAdminClient " + email);
            User recver = null;
            Long recverId = null;
            try {
                recver = userService.getUserByEmail(email);
                recverId = recver.getId();
                if (recver == null) continue;
            }catch (Exception e) {
                continue;
            }
//            System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 4");
            ChatRoom chatRoom = socketService.saveChatRoom(senderId, recverId);
            if (sent == false) {
                chat = socketService.saveChat(senderId, recverId, message, chatRoom, clientIp);
                sent = true;
            }
//            System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 4-2");
            socketService.saveChatRoom(senderId, recverId);
            Set<String> socketSet = socketService.getSocketSetByUserPk(recverId);
            if (socketSet == null)
                continue;
//            System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 5");
            chat.setReceiver(recver);
            String chatJson = convertChat2ChatJson(chat);
//            System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 6");
            for (String target : socketSet){
//                System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 7");
                messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);
//                System.out.println("sendMessageToAdminClient&&&&&&&&&&&&& 8");
            }
        }
    }

    private String convertChat2ChatJson(Chat chat) throws JsonProcessingException {
        ChatDto chatDto = new ChatDto();
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setId(chat.getChatRoom().getId());
        chatDto.setChatRoom(chatRoomDto);
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

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<Chat>> getChatsByUserId(HttpServletRequest request, @PathVariable Long roomId) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<List<Chat>>(socketService.getChatsByRoomId(roomId), HttpStatus.OK);
    }

    @GetMapping("/chat/general")
    public ResponseEntity<List<Chat>> getChatsByUserId(HttpServletRequest request) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<List<Chat>>(socketService.getChatsByUserId(myId), HttpStatus.OK);
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

    @DeleteMapping("/chatroom/delete/{roomId}")
    public ResponseEntity<String> deleteChatRoom(HttpServletRequest request,@PathVariable Long roomId) {
        long myId = -1;
        User user = null;
        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        socketService.deleteChatRoom(roomId);
        return ResponseEntity.ok("채팅방 해산 성공");
    }

//    @GetMapping("/chat/admin")
//    public ResponseEntity<List<Chat>> getChatsFromAdmin(HttpServletRequest request) {
//        long myId = -1;
//        User user = null;
//
//        try {
//            myId = userService.getIdByCookies(request.getCookies());
//            user = userService.getUserById(myId);
//            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        List<Chat> chatList = new ArrayList<>();
//        for (String email : userService.getAdminEmails()){
//            Long userId = null;
//            try {
//                userId = userService.getUserByEmail(email).getId();
//                if (userId == null) continue;
//            }catch (Exception e) {
//                continue;
//            }
//            chatList.addAll(socketService.getChats(myId, userId));
//        }
//        return new ResponseEntity<List<Chat>>(chatList, HttpStatus.OK);
//    }


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
