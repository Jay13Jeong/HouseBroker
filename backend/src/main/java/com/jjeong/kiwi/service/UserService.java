package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.domain.UserDto;
import com.jjeong.kiwi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final Set<String> adminEmails = new HashSet<>(Arrays.asList(System.getenv("ADMIN_EMAIL").split(",")));

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
//        user.setConnectCount(0);
//        user.setSocketId("");
        if ((!signupRequest.getEmail().isEmpty()) && (signupRequest.getEmail() != null) &&
                adminEmails.contains(signupRequest.getEmail()) == true) {
            user.setPermitLevel(10);
        } else user.setPermitLevel(1);
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByAuthId(String authId) {
        return userRepository.findUserByAuthid(authId);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findUserById(id);
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername((user.getUsername()));
        return userDto;
    }

    public int getUserPermit(long id) {
        return userRepository.findUserById(id).getPermitLevel();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public long getIdByCookies(Cookie[] cookies) {
        String token = "";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    break;
                }
            }
        } else {
            throw new RuntimeException("user not found");
        }

        return (this.getUserPrimaryKeyByJwt(token));
    }

    public long getUserPrimaryKeyByJwt(String token){
        if (token.isEmpty())
            throw new RuntimeException();

        String payload = authService.extractSubject(token).getSubject();

        long id = 0;
        int startIndex = payload.indexOf("id=") + 3;  // "id=" 다음 문자의 인덱스를 구합니다.
        int endIndex = payload.indexOf(",", startIndex);  // 쉼표 이전의 인덱스를 구합니다.

        if (endIndex == -1)
            endIndex = payload.length() - 1;

        id = Long.parseLong(payload.substring(startIndex, endIndex));

        if (id == 0)
            throw new RuntimeException("user not found");
        return  id;
    }
}