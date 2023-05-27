package com.jjeong.kiwi.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveUser() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        // When
        User savedUser = userRepository.save(user);

        // Then
        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(user.getUsername(), savedUser.getUsername());
    }

    @Test
    public void testFindUserById() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Then
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(savedUser.getId(), foundUser.getId());
        Assertions.assertEquals(savedUser.getEmail(), foundUser.getEmail());
        Assertions.assertEquals(savedUser.getUsername(), foundUser.getUsername());
    }

    @Test
    public void testFindUserByNonExistingId() {
        // When
        User foundUser = userRepository.findById(999L).orElse(null);

        // Then
        Assertions.assertNull(foundUser);
    }

    @Test
    public void testExistsByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        Assertions.assertTrue(exists);
    }

    @Test
    public void testExistsByNonExistingEmail() {
        // When
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");

        // Then
        Assertions.assertFalse(exists);
    }

}