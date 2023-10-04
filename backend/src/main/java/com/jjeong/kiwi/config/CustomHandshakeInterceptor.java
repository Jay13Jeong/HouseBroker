package com.jjeong.kiwi.config;

import com.jjeong.kiwi.domain.StompPrincipal;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
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
//        System.out.println("#################### start");
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
                            if (socketService.getSocketSetByUserPk(userPk).size() > 50) return false;
                        } catch (Exception e) {}
//                        String socketId = this.getWebSocketKey(request);
//                        attributes.put("socketId", socketId);
//                        socketService.addSocketAndUserPkMap(socketId, userPk);
                        String userPrincipalId = UUID.randomUUID().toString();
                        socketService.addUserPkAndSocketMap(userPk, userPrincipalId);
                        attributes.put("user", new StompPrincipal(userPrincipalId));
                        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
                        accessor.setUser(new StompPrincipal(String.valueOf(userPk)));
                        attributes.put(SimpMessageHeaderAccessor.USER_HEADER, accessor.getUser());
//                        System.out.println("####################" + userPk);
                        break;
                    }
                }
            }
        }
//        System.out.println("#################### end");
        //헨드쉐이크 허용.
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
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

}