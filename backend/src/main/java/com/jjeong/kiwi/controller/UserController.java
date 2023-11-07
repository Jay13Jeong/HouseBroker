package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.UserDto;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignupRequest signupRequest) {
        if (authService.confirmEmail(signupRequest) == false){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이메일 인증 실패.");
        }
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이미 사용 중인 이메일입니다.");
        }
        if (userService.createUser(signupRequest, true) == false){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("유저정보 생성 실패.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("회원가입이 성공적으로 완료되었습니다.");
    }

//    @PostMapping("/")
//    public ResponseEntity<String> signIn(@ModelAttribute SignupRequest signupRequest) {
//        if (userService.)
//        return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
//    }

    @GetMapping("/")
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        long id = -1;
        UserDto userDto = null;

        try {
            id = userService.getIdByCookies(request.getCookies());
            userDto = userService.getUserDtoById(id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/dormant")
    public ResponseEntity<String> returningUser(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());
        userService.returningUser(id);
        return ResponseEntity.ok("회원복귀 성공");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());
        userService.dormantUser(id);
//        userService.deleteUser(id);
        return ResponseEntity.ok("회원탈퇴 성공");
    }

    @GetMapping("/permit")
    public ResponseEntity<String> getUserPermit(HttpServletRequest request) {
        long id = userService.getIdByCookies(request.getCookies());
        return ResponseEntity.ok(String.valueOf(userService.getUserPermit(id)));
    }

}

