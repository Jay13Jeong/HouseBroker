package com.jjeong.kiwi.controller;

import static org.junit.jupiter.api.Assertions.*;
import com.jjeong.kiwi.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatControllerTest2 {


    @Mock
    private SimpMessagingTemplate mockSimpMessagingTemplate;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    private ChatController chatController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        chatController = new ChatController();
        chatController.setSimpMessagingTemplate(mockSimpMessagingTemplate);
    }


    @Test
    public void testReceivePublicMessage() {
        Message message = new Message();
        message.setSender("John");
        message.setReceiver(null);
        message.setMessage("Hello, public!");
        message.setDate("2023-05-25");
        message.setStatus("Sent");

        Message result = chatController.receivePublicMessage(message);

        Mockito.verify(mockSimpMessagingTemplate).convertAndSend(
                Mockito.eq("/chatroom/public"), Mockito.eq(message));

        assertEquals(message, result);
    }

    @Test
    public void testReceivePrivateMessage() {
        Message message = new Message();
        message.setSender("John");
        message.setReceiver("Jane");
        message.setMessage("Hello, private!");
        message.setDate("2023-05-25");
        message.setStatus("Sent");

        Message result = chatController.receivePrivateMessage(message);

        Mockito.verify(mockSimpMessagingTemplate).convertAndSendToUser(
                Mockito.eq("Jane"), Mockito.eq("/private"), Mockito.eq(message));

        assertEquals(message, result);
    }
}