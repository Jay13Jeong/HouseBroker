package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findUserByAuthid(String authId);

    void deleteById(Long id);

    User findUserById(Long id);
}
