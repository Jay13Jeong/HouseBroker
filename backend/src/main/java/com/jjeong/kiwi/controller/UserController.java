package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.service.AuthService;
import com.jjeong.kiwi.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    private final AuthService authService;

    @PostMapping("/signup")
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

    @GetMapping("/")
    public ResponseEntity<User> getUserInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = "";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        System.out.println("val:$" + token + "$");
        if (token.equals(""))
            throw new RuntimeException();

        System.out.println("getUserInfo part 1 =============");

        // 토큰에서 페이로드 추출
        String payload = authService.extractSubject(token).getSubject();

        System.out.println("getUserInfo part 2 =============");
        System.out.println(payload);

        long id = 0;
        int startIndex = payload.indexOf("id=") + 3;  // "id=" 다음 문자의 인덱스를 구합니다.
        int endIndex = payload.indexOf(",", startIndex);  // 쉼표 이전의 인덱스를 구합니다.

        if (endIndex == -1)
            endIndex = payload.length() - 1;

        id = Long.parseLong(payload.substring(startIndex, endIndex));

        if (id == 0)
            throw new RuntimeException("user not found");

        // 서비스 호출하여 사용자 정보 검색
        User user = userService.getUserById(id);

        if (user != null) {

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //권한 추가하기**
    //**delete method로 바꾸기 delete문구도 지우기**
    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/userList"; // 사용자 목록 페이지로 리다이렉트
    }

}
