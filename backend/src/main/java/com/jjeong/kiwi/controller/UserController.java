package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.domain.UserDto;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
        // 회원가입 로직 구현
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이미 사용 중인 이메일입니다.");
        }
        userService.createUser(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("회원가입이 성공적으로 완료되었습니다.");
    }

    @GetMapping("/")
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());

        // 서비스 호출하여 사용자 정보 검색
        UserDto userDto = userService.getUserDtoById(id);

        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());
        userService.deleteUser(id);
        return ResponseEntity.ok("회원탈퇴 성공");
    }

    @GetMapping("/permit")
    public ResponseEntity<String> getUserPermit(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());
        return ResponseEntity.ok(String.valueOf(userService.getUserPermit(id)));
    }

}

