package com.jjeong.kiwi.config;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;
    private final SocketService socketService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        //헨드쉐이크 중 jwt값 읽어오기.
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        if (cookieHeaders != null) {
            for (String cookieHeader : cookieHeaders) {
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2 && "jwt".equals(parts[0])) {
                        String jwtValue = parts[1];
                        long userPk = -1;
                        try {
                            userPk = userService.getUserPrimaryKeyByJwt(jwtValue);
                        }catch (Exception e){ break; }
                        attributes.put("jwt", jwtValue);
                        try {
                            if (socketService.getUserPkAndSocketMap(userPk).size() > 50) return false;
                        } catch (Exception e) {}
//                        System.out.println("[[[[[[[[[[[" + userPk + "]]]]]]]]]]]]]]]]");
                        String socketId = this.getWebSocketKey(request);
//                        String socketId = UUID.randomUUID().toString();
//                        while (socketService.getUserPkBySocketIdMap(socketId) != -1L){
//                            socketId = UUID.randomUUID().toString();
//                        }
//                        System.out.println("[[[[[[[[[[[" + 1111 + "]]]]]]]]]]]]]]]]");
                        attributes.put("socketId", socketId);
//                        User user = setSocketIdToUser(userPk, socketId);
//                        if (user != null && user.getConnectCount() < 50 && user.getConnectCount() >= 0){
//                            user.setConnectCount(user.getConnectCount() + 1);
//                            userService.saveUser(user);
//                            socketService.addSocketAndUserPkMap(socketId, user.getId());
//                            System.out.println("=================" + user.getConnectCount() + "==================");
//                            System.out.println(user.getSocketId());
//                        } else {
//                            return false;
//                        }
                        socketService.addSocketAndUserPkMap(socketId, userPk);
//                        System.out.println("[[[[[[[[[[[" + 2222 + "]]]]]]]]]]]]]]]]");
//                        System.out.println("=================" + socketService.getUserPkAndSocketMap(userPk).size() + "==================");
//                        System.out.println("[[[[[[[[[[[" + 3333 + "]]]]]]]]]]]]]]]]");
//                        System.out.println(socketService.getUserPkAndSocketMap(userPk).toString());
                        break;
                    }
                }
            }
        }
        //헨드쉐이크 허용.
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // 핸드쉐이크 이후에 수행할 작업을 구현할 수 있습니다.
    }

    private String getWebSocketKey(ServerHttpRequest request) {
        List<String> Headers = request.getHeaders().get("Sec-WebSocket-Key");
        if (Headers != null) {
            for (String webSocketKey : Headers) {
//                System.out.println("========= !!!!!!!! ==============");
//                System.out.println("=======" + webSocketKey);
                return webSocketKey;
            }
        }
        return UUID.randomUUID().toString();
    }

//    private User setSocketIdToUser(long userPk, String socketId){
//        try {
//            User user = userService.getUserById(userPk);
//            user.setSocketId(user.getSocketId() + ',' + socketId);
//            return user;
//        }catch (Exception e){
//
//        }
//        return null;
//    }
}