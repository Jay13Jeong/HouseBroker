package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.dto.SignupRequest;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class) // junit5에서 모키토 사용 선언.
class AuthControllerTest {

  @Mock
  private AuthService authService;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthController authController;

  @Test
  void signInValidUserReturnsOk() throws Exception {
    // 가상의 유저.
    User mockUser = new User();
    mockUser.setEmail("test@example.com");
    mockUser.setUsername("tester");
    // 가상의 유저 로그인 폼.
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("test@example.com");
    signupRequest.setPassword("password");

    // getUserByEmailAndPwd의 반환값을 가상의 유저로.
    when(userService.getUserByEmailAndPwd(any(SignupRequest.class))).thenReturn(mockUser);

    // Act & Assert
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    ResultActions resultActions = mockMvc.perform(post("/auth/login") //post로 엔드포인트 접근.
            .contentType(MediaType.APPLICATION_JSON) //페이로드타입 json으로 표현
            .content(new ObjectMapper().writeValueAsString(signupRequest))) //페이로드 넣기.
        .andExpect(status().isOk()); // 기대값은 200.
  }

  @Test
  void signInInvalidUserReturnsNotFound() throws Exception {
    // 입력된 로그인 폼.
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("nonexistent@example.com");
    signupRequest.setPassword("password");
    //서비스에 찾아보니 해당유저 없는 상황.
    when(userService.getUserByEmailAndPwd(any(SignupRequest.class))).thenReturn(null);

    //유저 매치 안되면 404반환하기.
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(signupRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void OkTestSendConfirmMailTest() throws Exception {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("exam@example.com");

    when(authService.emailValidate(any(String.class))).thenReturn(true);
    when(userService.getUserByEmail(any(String.class))).thenReturn(null);
    when(authService.sendConfirmMail(any(String.class))).thenReturn(true);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    mockMvc.perform(post("/auth/email/code")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(signupRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void emailInvalidateSendConfirmMailTest() throws Exception {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("exam123456789");

    when(authService.emailValidate(any(String.class))).thenReturn(false);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    mockMvc.perform(post("/auth/email/code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(signupRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void alreadyUseEmailSendConfirmMailTest() throws Exception {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("exam@example.com");

    User user = new User();

    when(authService.emailValidate(any(String.class))).thenReturn(true);
    when(userService.getUserByEmail(any(String.class))).thenReturn(user);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    mockMvc.perform(post("/auth/email/code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(signupRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void failConfirmMailSendConfirmMailTest() throws Exception {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("exam@example.com");

    when(authService.emailValidate(any(String.class))).thenReturn(true);
    when(userService.getUserByEmail(any(String.class))).thenReturn(null);
    when(authService.sendConfirmMail(any(String.class))).thenReturn(false);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    mockMvc.perform(post("/auth/email/code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(signupRequest)))
        .andExpect(status().isServiceUnavailable());
  }

}