package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    boolean existsByEmail(String email);

    Password findByEmail(String email);

    void deleteById(Long id);

    Password findUserById(Long id);
}
