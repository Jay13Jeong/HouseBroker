package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    Password findByEmail(String email);

    void deleteById(Long id);

}
