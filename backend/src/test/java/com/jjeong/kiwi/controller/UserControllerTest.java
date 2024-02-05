package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.dto.SignupRequest;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;


    @Test
    void signUp() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setUsername("jjeong");


//        given(userService.)
        when(userService.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userService.createUser(signupRequest, true)).thenReturn(true);

        // Act
//        ResultActions result = mockMvc.perform(post("/user/signup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(signupRequest)));
//
//        // Assert
//        result.andExpect(status().isCreated())
//                .andExpect(content().string("회원가입이 성공적으로 완료되었습니다."));
    }

    @Test
    void getUserInfo() throws Exception {
        User user = new User();

//        given(memberService.list()).willReturn(members);

//        mockMvc.perform(get("/api/user/"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("John")));
    }

    @Test
    void returningUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserPermit() {
    }
}