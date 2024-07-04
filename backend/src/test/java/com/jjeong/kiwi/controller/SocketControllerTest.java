package com.jjeong.kiwi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jjeong.kiwi.KiwiApplication;
import com.jjeong.kiwi.config.TestConfig;
import com.jjeong.kiwi.tool.GenerateJWT;
import com.jjeong.kiwi.tool.SampleData;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(
    classes = {KiwiApplication.class, TestConfiguration.class, TestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SocketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenerateJWT generateJWT;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private WebSocketHttpHeaders headers;

    @BeforeEach
    public void setup() {
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        // WebSocke 쿠키 설정
        headers = new WebSocketHttpHeaders();
//        headers.add(HttpHeaders.COOKIE, "myCookie=myValue");
        Cookie cookie = generateJWT.generateJWT_forTest();
        headers.add(HttpHeaders.COOKIE, String.format("%s=%s", cookie.getName(), cookie.getValue()));
    }

//    @Test
    @Order(1)
    public void testWebSocketEndpoint() throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();

        StompSession session = stompClient.connect(
            String.format("ws://localhost:%d/ws", port),
            headers,
            new StompSessionHandlerAdapter() {}
        ).get(1, TimeUnit.SECONDS);

        // 구독과 동시에 약간의 지연을 추가하여 메시지 전송 전에 구독이 완료되도록 함
        session.subscribe("/topic/hi", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                System.out.println("12345");
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("%%%%%%%%");
                future.complete(payload.toString());
            }
        });

        // 구독 완료 후 약간의 지연을 추가
        Thread.sleep(100); // 100ms 지연을 추가하여 구독이 완료되도록 함

        session.send("/app/hello", null);

        // 5초 동안 메시지를 기다림
        String message = future.get(5, TimeUnit.SECONDS);

        assertThat(message).isEqualTo("welcome!");
    }

    ////** 로그인하지 않은 사용자가 대화방 내용을 요청.
    @Test
    @Order(0)
    void getChatsByRoomId_401_test() throws Exception {
        Long roomId = 1L;
        mockMvc.perform(get("/chat/" + roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isUnauthorized());
    }

    ////** roomId에 대응하는 방이 없음.
    @Test
    @Order(0)
    void getChatsByRoomId_404_test() throws Exception {
        Long roomId = 1L;
        mockMvc.perform(get("/chat/" + roomId)
                .cookie(generateJWT.generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isNotFound());
    }

    ////** 권한을 가지고 채팅내역 요청.
    @Test
    @Order(1)
    void getChatsByRoomId_200_test() throws Exception {
        Long roomId = 1L;
        mockMvc.perform(get("/chat/" + roomId)
                .cookie(generateJWT.generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isOk());
    }

    ////** 일반 유저가 본인과 관리자 간의 대화 내역 불러오기 실패 : 로그인하지 않음.
    @Test
    @Order(0)
    void getChatsOfGeneralUser_401_test() throws Exception {
        mockMvc.perform(get("/chat/general")
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isUnauthorized());
    }

    ////** 일반 유저가 본인과 관리자 간의 대화 내역 불러오기 성공
    @Test
    @Order(2)
    void getChatsOfGeneralUser_200_test() throws Exception {
        mockMvc.perform(get("/chat/general")
                .cookie(generateJWT.generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isOk());
    }

    ////** 실패 : 관리자가 아닌 유저가 관리자 채팅 불러오기.
    @Test
    @Order(0)
    void getChatRooms_401_test() throws Exception {
        //** todo : 일반유저의 jwt 만들고 요청하기.
        mockMvc.perform(get("/chat/rooms")
//                .cookie(generateJWT.generateJWT_forTest())
                    .contentType(MediaType.APPLICATION_JSON)
                    .params(SampleData.getParams())
            )
            .andExpect(status().isUnauthorized());
    }

    ////** 관리자가 채팅 문의 내역 불러오기 성공.
    @Test
    @Order(1)
    void getChatRooms_200_test() throws Exception {
        mockMvc.perform(get("/chat/rooms")
                .cookie(generateJWT.generateJWT_forTest())
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isOk());
    }

    ////** 실패 : 관리자가 아닌 유저가 관리자 채팅 불러오기.
    @Test
    @Order(0)
    void deleteRealEstate_401_test() throws Exception {
        mockMvc.perform(get("/chat/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .params(SampleData.getParams())
            )
            .andExpect(status().isUnauthorized());
    }

    ////** 채팅방 해산 후, 재 조회시 404확인.
    @Test
    @Order(3)
    @Transactional
    void deleteRealEstate_204_test() throws Exception {
        Long roomId = 1L;
        mockMvc.perform(delete("/chatroom/delete/" + roomId)
                .cookie(generateJWT.generateJWT_forTest())
            )
            .andExpect(status().isNoContent());
        mockMvc.perform(get(
                "/chat/" + roomId))
            .andExpect(status().isNotFound());
    }


}