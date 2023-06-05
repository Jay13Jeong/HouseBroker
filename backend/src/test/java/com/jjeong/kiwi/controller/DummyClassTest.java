package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import com.jjeong.kiwi.service.UserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class DummyClassTest {

    @Autowired
    private DummyClass dummyClass;

    @Test
    void hello() {
        assertThat(dummyClass.hello()).isEqualTo("helloo");
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testExistsByEmail() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = userService.existsByEmail(email);

        assertThat(exists).isTrue();
    }

    @Test
    void testCreateUser() {
        User user = new User();
        // 유저 생성에 필요한 필드 설정

        when(userRepository.save(user)).thenReturn(user);

        boolean created = userService.createUser(user);

        assertThat(created).isTrue();
    }
}