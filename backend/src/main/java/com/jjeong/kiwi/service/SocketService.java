package com.jjeong.kiwi.service;

import lombok.RequiredArgsConstructor;
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

    public void addSocketAndUserPkMap(String socketId, Long userPk){
        socketAndUserPkMap.put(socketId, userPk);
        this.addUserPkAndSocketMap(userPk,socketId);
    }

    public void delSocketAndUserPkMap(String socketId){
        Long userPk = this.getUserPkBySocketId(socketId);
        if (userPk != -1L) this.delUserPkAndSocketMap(userPk, socketId);
        socketAndUserPkMap.remove(socketId);
        socketSessions.remove(socketId);
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
}
