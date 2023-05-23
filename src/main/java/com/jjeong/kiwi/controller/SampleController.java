package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SampleController {

        private final UserServices userService;

        public SampleController(UserServices userService) {
            this.userService = userService;
        }

        @PostMapping
        public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
            // 회원가입 로직 구현
            if (userService.existsByEmail(signupRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("이미 사용 중인 이메일입니다.");
            }

            User user = new User();
            user.setEmail(signupRequest.getEmail());
            user.setPassword(signupRequest.getPassword());
            // 추가 필요한 사용자 정보 설정

            userService.createUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("회원가입이 성공적으로 완료되었습니다.");
        }
}
