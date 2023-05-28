package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsByEmail_ExistingEmail_ShouldReturnTrue() {
        // Given
        String email = "test@example.com";
        User user = new User("testuser", email, "password");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByEmail_NonExistingEmail_ShouldReturnFalse() {
        // Given
        String email = "nonexisting@example.com";

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

}