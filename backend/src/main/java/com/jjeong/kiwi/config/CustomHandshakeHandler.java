package com.jjeong.kiwi.config;

import com.jjeong.kiwi.domain.StompPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // JWT 토큰을 헤더에서 추출하고 유저 ID를 얻어옵니다.
        String jwtToken = request.getHeaders().getFirst("Authorization");
        String userId = extractUserIdFromToken(jwtToken); // 추출하는 방법은 본인의 구현에 따라 다를 수 있습니다.

        // Principal 객체에 유저 ID 저장
        return new StompPrincipal(userId);
    }

    // JWT 토큰에서 유저 ID 추출 로직
    private String extractUserIdFromToken(String jwtToken) {
        // 본인의 JWT 파싱 및 유저 ID 추출 로직을 구현
        // 예: JWT 라이브러리 사용
        return  "";
    }

}
