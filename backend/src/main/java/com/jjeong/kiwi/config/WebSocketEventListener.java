package com.jjeong.kiwi.config;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SocketService socketService;
//    private final UserService userService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String socketId = (String) accessor.getSessionAttributes().get("socketId");
//        System.out.println("WebSocket connection closed. ============");
        if (socketId != null) {
            socketService.delSocketAndUserPkMap(socketId);
//            long userPk = socketService.getUserPkBySocketIdMap(socketId);
//            User user = userService.getUserById(userPk);
//            String[] socketIds = user.getSocketId().split(",");
//            int newCount = user.getConnectCount();
//            if (newCount < 1){
//                System.out.println("user conn count underflow :" + user.getEmail());
////                if (socketIds.length > 0){
////                    user.setSocketId(socketIds[socketIds.length - 1]);
////                }
//                user.setConnectCount(0);
//                user.setSocketId("");
//            } else if (newCount > 50) {
//                newCount = 1;
//                System.out.println("user conn count overflow :" + user.getEmail());
////                if (socketIds.length > 0){
////                    user.setSocketId(socketIds[socketIds.length - 1]);
////                }
//                user.setConnectCount(0);
//                user.setSocketId("");
//            } else {
//                List<String> socketIdList = new ArrayList<>(Arrays.asList(socketIds));
//                while (socketIdList.contains(socketId)) { //중복도 삭제.
//                    socketIdList.remove(socketId);
//                }
//                user.setSocketId(socketIdList.stream().collect(Collectors.joining(",")));
//                user.setConnectCount(newCount - 1);
//            }
//            socketService.delSocketAndUserPkMap(socketId);
//            userService.saveUser(user);
        }
    }
}
