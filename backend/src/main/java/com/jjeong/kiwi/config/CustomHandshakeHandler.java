package com.jjeong.kiwi.config;

import com.jjeong.kiwi.domain.StompPrincipal;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final UserService userService;
    private final SocketService socketService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        System.out.println("@@@@@@ determineUser start");
        // JWT 토큰을 헤더에서 추출하고 유저 ID를 얻어옵니다.
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        String userPrincipalId = UUID.randomUUID().toString();
        if (cookieHeaders != null) {
            for (String cookieHeader : cookieHeaders) {
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2 && "jwt".equals(parts[0])) {
//                        System.out.println("^^^^^^^^^^^ determineUser ^^^^^^^^^^^^^^^^^");
                        String jwtValue = parts[1];
                        long userPk = -1;
                        try {
                            userPk = userService.getUserPrimaryKeyByJwt(jwtValue);
                        }catch (Exception e){ break; }
//                        userPrincipalId = String.valueOf(userPk);
//                        attributes.put("jwt", jwtValue);
                        attributes.put("session-id", userPrincipalId);
                        socketService.addSocketAndUserPkMap(userPrincipalId,userPk);
                        socketService.addUserPkAndSocketMap(userPk, userPrincipalId);
                        break;
                    }
                }
            }
        }
        // Principal 객체에 유저 ID 저장
//        System.out.println("@@@@@@ determineUser " + userPrincipalId);
        return new StompPrincipal(userPrincipalId);
    }


}
