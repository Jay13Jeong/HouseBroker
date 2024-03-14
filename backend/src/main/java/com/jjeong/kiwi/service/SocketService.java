package com.jjeong.kiwi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.dto.ChatDto;
import com.jjeong.kiwi.dto.ChatRoomDto;
import com.jjeong.kiwi.model.Chat;
import com.jjeong.kiwi.model.ChatRoom;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.repository.ChatRepository;
import com.jjeong.kiwi.repository.ChatRoomRepository;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SocketService {
    private static final Map<String, Long> socketAndUserPkMap = new ConcurrentHashMap<>();
    private static final Map<Long, Set<String>> userPkAndSocketMap = new ConcurrentHashMap<>();
    private static final Map<String, String> socketAndUserIp = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;
    private final RedisTemplate<String, Object> redisTemp;

    public void putToSocketAndUserPkMap(String socketId, Long userPk){
        socketAndUserPkMap.put(socketId, userPk);
    }

    public void delToSocketAndUserPkMap(String socketId){
        socketAndUserPkMap.remove(socketId);
    }

    public void putToSocketAndUserIp(String socketId, String ip){
        socketAndUserIp.put(socketId, ip);
    }

    public void delToSocketAndUserIp(String socketId){
        socketAndUserIp.remove(socketId);
    }

    public void putToUserPkAndSocketMap(Long userPk, Set<String> socketIds){
        userPkAndSocketMap.put(userPk, socketIds);
    }

    public void delToUserPkAndSocketMap(Long userPk){
        userPkAndSocketMap.remove(userPk);
    }

    public void addSocketAndUserPkMap(String socketId, Long userPk){
        this.putToSocketAndUserPkMap(socketId, userPk);
        this.addUserPkAndSocketMap(userPk,socketId);
    }

    public void delSocketAndUserPkMap(String socketId){
        Long userPk = this.getUserPkBySocketId(socketId);
        if (userPk != -1L) this.delUserPkAndSocketMap(userPk, socketId);
        this.delToSocketAndUserPkMap(socketId);
    }

    public void addSocketAndUserIp(String socketId, String ip){
        this.putToSocketAndUserIp(socketId, ip);
    }

    public void delSocketAndUserIp(String socketId){
        this.delToSocketAndUserIp(socketId);
    }

    public String getIpBySocketId(String socketId){
        try {
            return  socketAndUserIp.get(socketId);
        } catch (Exception e) {
            logger.error("notFound:getIpBySocketId", e);
            return "0.0.0.0";
        }
    }

    public Long getUserPkBySocketId(String socketId){
        try {
            return  socketAndUserPkMap.get(socketId);
        } catch (Exception e) {
            logger.error("getUserPkBySocketId", e);
            return -1L;
        }
    }

    public void addUserPkAndSocketMap(Long userPk, String socketId) {
        Set<String> tmp = null;
        try{
            tmp = userPkAndSocketMap.get(userPk);
            tmp.add(socketId);
            this.putToUserPkAndSocketMap(userPk, tmp);
        }catch (Exception e){
            logger.error("addUserPkAndSocketMap", e);
            tmp = new HashSet<>();
            tmp.add(socketId);
            this.putToUserPkAndSocketMap(userPk, tmp);
        }
    }

    public  void delUserPkAndSocketMap(Long userPk, String socketId){
        Set<String> tmp = null;
        try{
            tmp = userPkAndSocketMap.get(userPk); // 얕은 복사처리.
            if (tmp.size() <= 1) {
                this.delToUserPkAndSocketMap(userPk);
                return;
            }
            tmp.remove(socketId);
            this.putToUserPkAndSocketMap(userPk, tmp);
        }catch (Exception e){
            logger.error("fail:delUserPkAndSocketMap", e);
        }
    }

    public  Set<String> getSocketSetByUserPk(Long userPk) {
        return userPkAndSocketMap.get(userPk);
    }

    @Transactional
    public ChatRoom saveChatRoom(Long senderId, Long receiverId) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        List<User> users1 = new ArrayList<>();
        users1.add(sender);
        ChatRoom chatRoom = null;
        ChatRoom commonChatRoom = this.loadChatRoomByRoomName(sender.getEmail());
        if (commonChatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.addUser(sender);
            chatRoom.addUser(receiver);
            if (userService.getAdminEmails().contains(sender.getEmail())){
                chatRoom.setRoomName(receiver.getEmail());
            } else {
                chatRoom.setRoomName(sender.getEmail());
            }
            chatRoom = chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = commonChatRoom;
            if (!chatRoom.haveUser(receiver)){
                chatRoom.addUser(receiver);
                chatRoomRepository.save(chatRoom);
            }
            System.out.println(commonChatRoom.getRoomName());
        }
        return chatRoom;
    }

    @Transactional
    public Chat saveChat(Long senderId, Long receiverId, String message, ChatRoom chatRoom, String senderIp) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        Chat chat = new Chat();
        chat.setMessage(message);
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setChatRoom(chatRoom);
        chat.setSenderIp(senderIp);
        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatsByRoomId(Long roomId) {
        ChatRoom commonChatRooms = chatRoomRepository.findChatRoomById(roomId);

        if (commonChatRooms == null) {
            return Collections.emptyList();
        }

        ChatRoom chatRoom = commonChatRooms;
        List<Chat> messages = chatRepository.findByChatRoom(chatRoom);
        List<Chat> newMessages = new ArrayList<>();

        for (Chat message : messages) {
            message.setChatRoom(null);
            newMessages.add(message);
        }
        return newMessages;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(Long roomId) {
        return chatRoomRepository.findChatRoomById(roomId);
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatsByUserId(long myId) {
        List<ChatRoom> commonChatRooms = this.getChatRooms(myId);
        if (commonChatRooms.isEmpty()){
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

    @Transactional(readOnly = true)
    public ChatRoom loadChatRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId);
        if (chatRoom != null){
            Hibernate.initialize(chatRoom.getUsers());
        }
        return chatRoom;
    }

    @Transactional
    public ChatRoom loadChatRoomByRoomName(String roomName) {
            List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomByRoomName(roomName);
            ChatRoom chatRoom = null;
            if (chatRooms != null && chatRooms.size() > 0) {
                if (chatRooms.size() > 1) {
                    logger.info("delete:loadChatRoomByRoomName:unuseRooms");
                    for (int i = 1; i < chatRooms.size(); i++){
                        chatRoomRepository.delete(chatRooms.get(i));
                    }
                }
                chatRoom = chatRooms.get(0);
                System.out.println();
                // 세션이 열려있는 상태에서 지연로딩된 엔티티 초기화
                Hibernate.initialize(chatRoom.getUsers());
            }
            return chatRoom;
    }

    @Transactional
    public void deleteChatRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }

    public String convertChat2ChatJson(Chat chat) throws JsonProcessingException {
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

    public boolean checkSender(Principal principal, SimpMessagingTemplate messagingTemplate) throws JsonProcessingException {
        if (this.getUserPkBySocketId(principal.getName()) == null){
            ChatDto chatDto = new ChatDto();
            chatDto.setMessage("로그인 후 가능합니다.");
            String chatJson = objectMapper.writeValueAsString(chatDto);
            messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/message", chatJson);
            return false;
        }
        return true;
    }
}
