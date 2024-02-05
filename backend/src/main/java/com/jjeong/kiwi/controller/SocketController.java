package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.dto.ChatDto;
import com.jjeong.kiwi.dto.ChatRoomDto;
import com.jjeong.kiwi.model.*;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketService socketService;
    private final UserService userService;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/hi")
    public String sendChatMessage() {
        return "";
    }


    @MessageMapping("/send/room/{roomId}")
    public void sendMessageToClient(
            @DestinationVariable Long roomId,
            Principal principal,
            String message) throws JsonProcessingException {
        if (!this.checkSender(principal)) return;
        String clientIp = this.getClientIp(principal.getName());
        ChatRoom chatRoom = socketService.loadChatRoomById(roomId);
        if (chatRoom == null) {
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
        System.out.println(headers.toString());
        String clientIp = this.getClientIp(principal.getName());
        Long senderId = socketService.getUserPkBySocketId(principal.getName());
        Chat chat = null;
        boolean sent = false;
        for (String email : userService.getAdminEmails()){
            User recver = null;
            Long recverId = null;
            try {
                recver = userService.getUserByEmail(email);
                recverId = recver.getId();
                if (recver == null) continue;
            }catch (Exception e) {
                logger.error("sendMessageToAdminClient", e);
                continue;
            }
            ChatRoom chatRoom = socketService.saveChatRoom(senderId, recverId);
            if (sent == false) {
                chat = socketService.saveChat(senderId, recverId, message, chatRoom, clientIp);
                sent = true;
            }
            socketService.saveChatRoom(senderId, recverId);
            Set<String> socketSet = socketService.getSocketSetByUserPk(recverId);
            if (socketSet == null)
                continue;
            chat.setReceiver(recver);
            String chatJson = convertChat2ChatJson(chat);
            for (String target : socketSet){
                messagingTemplate.convertAndSendToUser(target, "/topic/message", chatJson);
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
            logger.error("getChatsByUserId", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<List<Chat>>(socketService.getChatsByRoomId(roomId), HttpStatus.OK);
    }

    @GetMapping("/chat/general")
    public ResponseEntity<List<Chat>> getChatsOfGeneralUser(HttpServletRequest request) {
        long myId = -1;
        User user = null;

        try {
            myId = userService.getIdByCookies(request.getCookies());
            user = userService.getUserById(myId);
            if (user.isDormant()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e) {
            logger.error("getChatsOfGeneralUser", e);
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
            logger.error("getChatRooms", e);
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
            logger.error("deleteChatRoom", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        socketService.deleteChatRoom(roomId);
        return ResponseEntity.ok("채팅방 해산 성공");
    }

}
