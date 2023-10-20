package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.Chat;
import com.jjeong.kiwi.domain.ChatRoom;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.ChatRepository;
import com.jjeong.kiwi.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SocketService {
    private static final Map<String, Long> socketAndUserPkMap = new HashMap<>();
    private static final Map<Long, Set<String>> userPkAndSocketMap = new HashMap<>();
    private static final Map<String, WebSocketSession> socketSessions = new ConcurrentHashMap<>();

    public void addSocketSession(String socketId, WebSocketSession wss){
        socketSessions.put(socketId, wss);
    }

    public void delSocketSession(String socketId){
        socketSessions.remove(socketId);
    }

    public WebSocketSession getWebSocketSession(String socketId) {
        return  socketSessions.get(socketId);
    }

    private final UserService userService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

    public void addSocketAndUserPkMap(String socketId, Long userPk){
        socketAndUserPkMap.put(socketId, userPk);
        this.addUserPkAndSocketMap(userPk,socketId);
    }

    public void delSocketAndUserPkMap(String socketId){
        Long userPk = this.getUserPkBySocketId(socketId);
        if (userPk != -1L) this.delUserPkAndSocketMap(userPk, socketId);
        socketAndUserPkMap.remove(socketId);
//        socketSessions.remove(socketId);
    }

    public Long getUserPkBySocketId(String socketId){
        try {
            return  socketAndUserPkMap.get(socketId);
        } catch (Exception e) {
            return -1L;
        }
    }

    public void addUserPkAndSocketMap(Long userPk, String socketId) {
        Set<String> tmp = null;
        try{
            tmp = userPkAndSocketMap.get(userPk);
            tmp.add(socketId);
            userPkAndSocketMap.put(userPk, tmp);
        }catch (Exception e){
            tmp = new HashSet<>();
            tmp.add(socketId);
            userPkAndSocketMap.put(userPk, tmp);
        }
//        tmp.add(socketId);
//        userPkAndSocketMap.put(userPk, tmp);
    }

    public  void delUserPkAndSocketMap(Long userPk, String socketId){
        Set<String> tmp = null;
        try{
            tmp = userPkAndSocketMap.get(userPk);
            tmp.remove(socketId);
            userPkAndSocketMap.put(userPk, tmp);
        }catch (Exception e){ }
    }

    public  Set<String> getSocketSetByUserPk(Long userPk) {
        return userPkAndSocketMap.get(userPk);
    }

    public Chat saveChat(Long senderId, Long receiverId, String message) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);

        List<User> users = new ArrayList<>();
        users.add(sender);
        users.add(receiver);

        ChatRoom chatRoom = null;
        List<ChatRoom> commonChatRooms = chatRoomRepository.findByUsersIn(users);
        if (commonChatRooms.isEmpty()) {
            chatRoom = new ChatRoom();
            System.out.println("######### saveChat1" + sender.getId());
            System.out.println("######### saveChat2" + receiver.getId());
            chatRoom.addUser(sender);
            chatRoom.addUser(receiver);
            if (userService.getAdminEmails().contains(sender.getEmail())){
                chatRoom.setRoomName(receiver.getEmail());
            } else {
                chatRoom.setRoomName(sender.getEmail());
            }
            chatRoom = chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = commonChatRooms.get(0);
        }
        Chat chat = new Chat();
        chat.setMessage(message);
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setChatRoom(chatRoom);

        return chatRepository.save(chat);
    }

    public List<Chat> getChats(long myId, Long id) {
        User me = userService.getUserById(myId);
        User targete = userService.getUserById(id);

        List<User> users = new ArrayList<>();
        users.add(me);
        users.add(targete);

        List<ChatRoom> commonChatRooms = chatRoomRepository.findByUsersIn(users);
        if (commonChatRooms.isEmpty()) {
            return Collections.emptyList();
        }
        ChatRoom chatRoom = commonChatRooms.get(0);
        List<Chat> messages = chatRepository.findByChatRoom(chatRoom);
        List<Chat> newMessages = new ArrayList<>();
        for (Chat message : messages) {
            message.setChatRoom(null);
            newMessages.add(message);
        }
        return newMessages;
    }

    public List<ChatRoom> getChatRooms(long myId) {
        User me = userService.getUserById(myId);
        List<User> users = new ArrayList<>();
        users.add(me);

        List<ChatRoom> commonChatRooms = chatRoomRepository.findByUsersIn(users);
        if (commonChatRooms.isEmpty()) {
            return Collections.emptyList();
        }
        List<ChatRoom> newChatRooms = new ArrayList<>();
        for (ChatRoom chatRoom : commonChatRooms) {
            Set<User> newUsers = new HashSet<>();
            for (User user : chatRoom.getUsers()){
                user.setChatRooms(null);
                newUsers.add(user);
            }
            chatRoom.setChats(null);
            chatRoom.setUsers(newUsers);
            newChatRooms.add(chatRoom);
        }
        return newChatRooms;
    }
}
