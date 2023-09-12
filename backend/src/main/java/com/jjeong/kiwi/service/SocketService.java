package com.jjeong.kiwi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SocketService {
    private static final Map<String, Long> socketAndUserPkMap = new HashMap<>();
    private static final Map<Long, Set<String>> userPkAndSocketMap = new HashMap<>();

    public void addSocketAndUserPkMap(String socketId, Long userPk){
        socketAndUserPkMap.put(socketId, userPk);
        this.addUserPkAndSocketMap(userPk,socketId);
    }

    public void delSocketAndUserPkMap(String socketId){
        Long userPk = this.getUserPkBySocketIdMap(socketId);
        if (userPk != -1L) this.delUserPkAndSocketMap(userPk, socketId);
        socketAndUserPkMap.remove(socketId);
    }

    public Long getUserPkBySocketIdMap(String socketId){
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

    public  Set<String> getUserPkAndSocketMap(Long userPk) {
        return userPkAndSocketMap.get(userPk);
    }
}
