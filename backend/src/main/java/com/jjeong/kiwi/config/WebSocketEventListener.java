package com.jjeong.kiwi.config;

import com.jjeong.kiwi.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SocketService socketService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String socketId = (String) accessor.getSessionAttributes().get("session-id");
        if (socketId != null) {
            socketService.delSocketAndUserPkMap(socketId);
            socketService.delSocketAndUserIp(socketId);
        }
    }
}
