package com.jjeong.kiwi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.SocketService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SocketService socketService;
//    private final UserService userService;

//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        System.out.println("&&&&&&&& handleWebSocketConnectListener &&&&&&&&&&&&");
//
//
//        System.out.println(event.getUser());
//        System.out.println(event.getSource());
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        System.out.println(sessionId);
//        System.out.println("&&&&&&&& handleWebSocketConnectListener2 &&&&&&&&&&&&");
//
//        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
//        GenericMessage generic = (GenericMessage) accessor.getHeader("simpConnectMessage");
//        Map attributes = (Map) generic.getHeaders().get("simpSessionAttributes");
//        String socketId = (String) attributes.get("socketId");
////        String sessionId = (String) generic.getHeaders().get("simpSessionId");
//
////        String socketId = "";
////        try {
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-0 &&&&&&&&&&&&");
////            String simpSessionAttributes = (String) headerAccessor.getSessionAttributes().get("simpSessionAttributes");
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-1 &&&&&&&&&&&&");
////            String simpSessionAttributes = (String) headerAccessor.getSessionAttributes().get("simpSessionAttributes");
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-1 &&&&&&&&&&&&");
////            String simpSessionAttributes = (String) headerAccessor.getSessionAttributes().get("simpSessionAttributes");
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-1 &&&&&&&&&&&&");
////            ObjectMapper objectMapper = new ObjectMapper();
////            Map<String, Object> sessionAttributesMap = objectMapper.readValue(simpSessionAttributes, Map.class);
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-2 &&&&&&&&&&&&");
////            socketId = (String) sessionAttributesMap.get("socketId");
////            System.out.println("&&&&&&&& handleWebSocketConnectListener2-3 &&&&&&&&&&&&");
////            System.out.println("socketId: " + socketId);
////        } catch (JsonProcessingException e) {
////            // JSON 처리 예외가 발생한 경우 여기에서 처리
////            e.printStackTrace();
////            return;
////        }
//        System.out.println("&&&&&&&& handleWebSocketConnectListener3 &&&&&&&&&&&&");
//        socketService.addSocketSession(socketId, (WebSocketSession) event.getSource()); // 클라이언트 세션을 맵에 저장
//        System.out.println("&&&&&&&& handleWebSocketConnectListener4 &&&&&&&&&&&&");
//    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String socketId = (String) accessor.getSessionAttributes().get("socketId");
        if (socketId != null) {
            socketService.delSocketAndUserPkMap(socketId);
        }
    }
}
