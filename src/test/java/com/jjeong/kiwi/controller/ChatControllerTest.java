package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReceivePublicMessage() {
        Message message = new Message(null, null, "Hello, public!", null, null);

        chatController.receivePublicMessage(message);

        verify(messagingTemplate).convertAndSend("/chatroom/public", message);
    }

//    private ChatController chatController;
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @BeforeEach
//    public void setUp() {
//        simpMessagingTemplate = mock(SimpMessagingTemplate.class);
//        chatController = new ChatController();
//        chatController.setSimpMessagingTemplate(simpMessagingTemplate);
//    }
//
//    @Test
//    public void testReceivePublicMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Message message = new Message();
//        message.setMessage("Hello, public!");
//
////        ChatController chatController = new ChatController();
//        Method method = ChatController.class.getDeclaredMethod("receivePublicMessage", Message.class);
//        method.setAccessible(true);
//        Message result = (Message) method.invoke(chatController, message);
//
//        verify(simpMessagingTemplate, times(1)).convertAndSend(eq("/chatroom/public"), eq(message));
//        // Assert any other assertions on the result if needed
//    }

//    @Test
//    public void testReceivePrivateMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Message message = new Message();
//        message.setMessage("Hello, private!");
//        message.setReceiver("targetName");
//
////        ChatController chatController = new ChatController();
//        Method method = ChatController.class.getDeclaredMethod("receivePrivateMessage", Message.class);
//        method.setAccessible(true);
//        Message result = (Message) method.invoke(chatController, message);
//
//        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(eq("targetName"), eq("/private"), eq(message));
//        // Assert any other assertions on the result if needed
//    }

}