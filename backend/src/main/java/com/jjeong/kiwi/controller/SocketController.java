package com.jjeong.kiwi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/hello") // 클라이언트의 메시지를 처리하는 엔드포인트
    @SendTo("/topic/hi") // 해당 목적지에 메시지 브로드캐스트
    public String sendChatMessage(String message) {
        System.out.println("hihi");
        // 받은 메시지를 모든 구독자에게 브로드캐스트
        return message;
    }
}
