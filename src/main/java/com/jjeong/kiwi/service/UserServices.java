package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    private UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean createUser(User user) {
        // 회원 생성 로직 작성
        // 필요한 유효성 검사 등을 수행한 후 회원을 생성하고 저장합니다.
        // 회원 생성에 성공하면 true를 반환하고, 실패하면 false를 반환합니다.
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}