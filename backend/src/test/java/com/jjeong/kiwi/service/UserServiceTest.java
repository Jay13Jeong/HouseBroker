package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void testExistsByEmail_ExistingEmail_ReturnsTrue() {
        String email = "example@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void testExistsByEmail_NonExistingEmail_ReturnsFalse() {
        String email = "example@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void testCreateUser_ValidUser_ReturnsTrue() {
        User user = new User("username", "example@example.com", "password");
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.createUser(user);

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUser_ExceptionThrown_ReturnsFalse() {
        User user = new User("username", "example@example.com", "password");
        when(userRepository.save(user)).thenThrow(new RuntimeException());

        boolean result = userService.createUser(user);

        assertFalse(result);
        verify(userRepository, times(1)).save(user);
    }
}