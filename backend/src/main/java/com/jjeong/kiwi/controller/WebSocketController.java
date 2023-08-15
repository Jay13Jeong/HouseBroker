package com.jjeong.kiwi.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/hi")
    public String processMessage(@Payload String message) throws Exception {
        System.out.println("\nConnectedd\n");
        Thread.sleep(500);
        return "Processed: " + message;
    }
}
