package com.jjeong.kiwi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerTest {
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    public void testSignUp_NewUser_SuccessfulRegistration() {
        String username = "example";
        String email = "example@example.com";
        String password = "password";

        SignupRequest signupRequest = new SignupRequest(username, email, password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        when(userService.existsByEmail(email)).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(true);

        ResponseEntity<String> response = userController.signUp(signupRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("회원가입이 성공적으로 완료되었습니다.", response.getBody());
        verify(userService, times(1)).existsByEmail(email);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testSignUp_ExistingEmail_ReturnsBadRequest() {
        String username = "example";
        String email = "example@example.com";
        String password = "password";

        SignupRequest signupRequest = new SignupRequest(username, email, password);

        when(userService.existsByEmail(email)).thenReturn(true);

        ResponseEntity<String> response = userController.signUp(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("이미 사용 중인 이메일입니다.", response.getBody());
        verify(userService, times(1)).existsByEmail(email);
        verify(userService, never()).createUser(any(User.class));
    }
}