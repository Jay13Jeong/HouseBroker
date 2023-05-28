package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Service
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean createUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}