package com.jjeong.kiwi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.dto.SignupRequest;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import org.jetbrains.annotations.Contract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
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
//        try {
//            System.out.println("Status Code: " + resultActions.andReturn().getResponse().getStatus());
//            System.out.println("Headers: " + resultActions.andReturn().getResponse().getHeaderNames());
//            String cookie = resultActions.andReturn().getResponse().getHeader("Set-Cookie");
//            assert cookie != null;
//            String jwt = extractJwtValue(cookie);
//            Map<String, String> jwtMap = userService.getUserInfoMapByJwt(jwt);
//            if (!testJWTinfo(jwtMap, signupRequest)){
//                throw new AssertionError("invalid jwt");
//            } else {
//                throw new AssertionError("valid jwt");
//            }
//        } catch (NullPointerException e){
//            System.out.println("not found jwt : " + e);
//        }
  }

//    private String extractJwtValue(String setCookieHeader) {
//        String[] cookieParts = setCookieHeader.split(";")[0].split("="); // 쿠키 형식에서 JWT 값 추출
//        if (cookieParts.length == 2 && cookieParts[0].trim().equals("jwt")) {
//            return cookieParts[1].trim();
//        } else {
//            throw new RuntimeException("JWT not found in Set-Cookie header");
//        }
//    }
//
//    /**
//     * @param tokenMap
//     * @param signupRequest
//     * @return
//     */
//    @Contract(pure = true)
//    private boolean testJWTinfo(Map<String, String> tokenMap, SignupRequest signupRequest){
//        try {
//            if (!tokenMap.get("name").equals(signupRequest.getUsername())){
//                return false;
//            }
//        } catch (Exception e){
//            return false;
//        }
//
//        return true;
//    }

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

}