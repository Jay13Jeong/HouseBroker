package com.jjeong.kiwi.config;

import com.jjeong.kiwi.dto.StompPrincipal;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CustomHandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // JWT 토큰을 헤더에서 추출하고 유저 ID를 얻어옵니다.
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        String userPrincipalId = UUID.randomUUID().toString();
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
                            if (userService.getUserById(userPk).isDormant()) break; //휴면 검사.
                        }catch (Exception e){
                            logger.error("determineUser", e);
                            break;
                        }
                        String clientIp = request.getRemoteAddress().getHostString();
                        socketService.addSocketAndUserIp(userPrincipalId,clientIp);
                        attributes.put("session-id", userPrincipalId);
                        socketService.addSocketAndUserPkMap(userPrincipalId,userPk);
                        socketService.addUserPkAndSocketMap(userPk, userPrincipalId);
                        break;
                    }
                }
            }
        }
        // Principal 객체에 유저 ID 저장
        return new StompPrincipal(userPrincipalId);
    }


}
