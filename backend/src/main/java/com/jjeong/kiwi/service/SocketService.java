package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.Chat;
import com.jjeong.kiwi.domain.ChatRoom;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.ChatRepository;
import com.jjeong.kiwi.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SocketService {
    private static final Map<String, Long> socketAndUserPkMap = new HashMap<>();
    private static final Map<Long, Set<String>> userPkAndSocketMap = new HashMap<>();

    private static final Map<String, String> socketAndUserIp = new HashMap<>();

    private final UserService userService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

//    public void getSize(){
//        System.out.println("***************************");
//        System.out.println(socketAndUserPkMap.size());
//        System.out.println(userPkAndSocketMap.size());
//        System.out.println(socketAndUserIp.size());
//        System.out.println("***************************");
//    }

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

    public void addSocketAndUserIp(String socketId, String ip){
        socketAndUserIp.put(socketId, ip);
    }

    public void delSocketAndUserIp(String socketId){
        socketAndUserIp.remove(socketId);
    }

    public String getIpBySocketId(String socketId){
        try {
            return  socketAndUserIp.get(socketId);
        } catch (Exception e) {
            System.out.println("notFound:getIpBySocketId");
            return "0.0.0.0";
        }
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
            if (tmp.size() <= 1) {
                userPkAndSocketMap.remove(userPk);
                return;
            }
            tmp.remove(socketId);
            userPkAndSocketMap.put(userPk, tmp);
        }catch (Exception e){
            System.out.println("fail:delUserPkAndSocketMap");
        }
    }

    public  Set<String> getSocketSetByUserPk(Long userPk) {
        return userPkAndSocketMap.get(userPk);
    }

    @Transactional
    public ChatRoom saveChatRoom(Long senderId, Long receiverId) {
        //기본적으로 채팅방을 만드는데
        //만약 센더이름의 채팅방이 있디면 센더의 채팅방을 그대로 이용
//        System.out.println("============getSocketSetByUserPk0");
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
//        System.out.println("============getSocketSetByUserPk0-1");
        List<User> users1 = new ArrayList<>();
        List<User> users2 = new ArrayList<>();
        users1.add(sender);
//        users2.add(receiver);
//        System.out.println("============getSocketSetByUserPk0-21 " + senderId);
//        System.out.println("============getSocketSetByUserPk0-22 " + receiverId);
        ChatRoom chatRoom = null;
        ChatRoom commonChatRoom = this.loadChatRoomByRoomName(sender.getEmail());
//        List<ChatRoom> senderChatRooms = chatRoomRepository.findByUsersIn(users1);
//        List<ChatRoom> receiverChatRooms = chatRoomRepository.findByUsersIn(users2);
//        List<ChatRoom> commonChatRooms = null;
//        if (senderChatRooms != null && receiverChatRooms != null){
//            senderChatRooms.retainAll(receiverChatRooms);
//            commonChatRooms = senderChatRooms;
//        }
//        System.out.println("============getSocketSetByUserPk0-3");
//        System.out.println("============getSocketSetByUserPk1 " + receiverId.toString());
//        System.out.println("============getSocketSetByUserPk0-4");
        if (commonChatRoom == null) {
//            System.out.println("============getSocketSetByUserPk 2");
            chatRoom = new ChatRoom();
//            System.out.println("######### saveChat1" + sender.getId());
//            System.out.println("######### saveChat2" + receiver.getId());
            chatRoom.addUser(sender);
            chatRoom.addUser(receiver);
//            System.out.println("######### saveChat3" );
            if (userService.getAdminEmails().contains(sender.getEmail())){
                chatRoom.setRoomName(receiver.getEmail());
            } else {
                chatRoom.setRoomName(sender.getEmail());
            }
//            System.out.println("######### saveChat4");
            chatRoom = chatRoomRepository.save(chatRoom);
//            chatRoom = chatRoomRepository.saveAndFlush(chatRoom);
//            System.out.println("============getSocketSetByUserPk3");
        } else {
//            System.out.println("============getSocketSetByUserPk4");
            chatRoom = commonChatRoom;
//            if (!chatRoom.getUsers().contains(receiver)){
//                chatRoom.addUser(receiver);
//            }
            if (!chatRoom.haveUser(receiver)){
                chatRoom.addUser(receiver);
                chatRoomRepository.save(chatRoom);
            }
//            System.out.println(commonChatRooms.toString());
            System.out.println(commonChatRoom.getRoomName());
//            System.out.println("============getSocketSetByUserPk5");
        }
//        System.out.println("============getSocketSetByUserPk6 ");
        return chatRoom;
    }

    public Chat saveChat(Long senderId, Long receiverId, String message, ChatRoom chatRoom, String senderIp) {
        //기본적으로 채팅방을 만드는데
        //만약 센더이름의 채팅방이 있디면 센더의 채팅방을 그대로 이용
//        System.out.println("============saveChat0");
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
//        System.out.println("============saveChat6 " + message);
        Chat chat = new Chat();
        chat.setMessage(message);
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setChatRoom(chatRoom);
        chat.setSenderIp(senderIp);
//        System.out.println("============saveChat7 ");
        return chatRepository.save(chat);
    }

//    public List<Chat> getChats(long myId, Long id) {
//        User me = userService.getUserById(myId);
//        User target = userService.getUserById(id);
//        ChatRoom commonChatRooms = null;
//
//        if (me.getPermitLevel() == 1 || me.getPermitLevel() == 0){
//            commonChatRooms = chatRoomRepository.findByRoomName(me.getEmail());
//        } else if (target.getPermitLevel() == 1 || target.getPermitLevel() == 0) {
//            commonChatRooms = chatRoomRepository.findByRoomName(target.getEmail());
//        } else {
//            System.out.println("((((((getChats " + me.getPermitLevel() + ' ' + target.getPermitLevel());
//            throw new RuntimeException("invalid chat request");
//        }
//
//        if (commonChatRooms == null) {
//            return Collections.emptyList();
//        }
//
//        ChatRoom chatRoom = commonChatRooms;
//        List<Chat> messages = chatRepository.findByChatRoom(chatRoom);
//        List<Chat> newMessages = new ArrayList<>();
//
//        for (Chat message : messages) {
//            message.setChatRoom(null);
//            newMessages.add(message);
//        }
//        return newMessages;
//    }

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

    public ChatRoom getChatRoomById(Long roomId) {
        return chatRoomRepository.findChatRoomById(roomId);
    }

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

    @Transactional
    public ChatRoom loadChatRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId);
        if (chatRoom != null){
//            System.out.println("============loadChatRoomById 1");
            Hibernate.initialize(chatRoom.getUsers());
//            System.out.println("============loadChatRoomById 2");
        }
        return chatRoom;
    }

    public ChatRoom loadChatRoomByRoomName(String roomName) {
//            System.out.println("============loadChatRoomByRoomName1");
            List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomByRoomName(roomName);
            ChatRoom chatRoom = null;
            if (chatRooms != null && chatRooms.size() > 0) {
                if (chatRooms.size() > 1) {
                    System.out.println("delete:loadChatRoomByRoomName:unuseRooms");
                    for (int i = 1; i < chatRooms.size(); i++){
                        chatRoomRepository.delete(chatRooms.get(i));
                    }
                }
                chatRoom = chatRooms.get(0);
//                System.out.println("============loadChatRoomByRoomName1-1" + chatRoom.getRoomName());
                System.out.println();
                // 세션이 열려있는 상태에서 지연로딩된 엔티티 초기화
                Hibernate.initialize(chatRoom.getUsers());
//                System.out.println("============loadChatRoomByRoomName1-2");
            }

//            System.out.println("============loadChatRoomByRoomName2");
            return chatRoom;
    }

    public void deleteChatRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }
}
