package com.jjeong.kiwi.service;

import com.jjeong.kiwi.domain.Password;
import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.domain.UserDto;
import com.jjeong.kiwi.repository.PasswordRepository;
import com.jjeong.kiwi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> adminEmails = new HashSet<>(Arrays.asList(System.getenv("ADMIN_EMAIL").split(",")));

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean createUser(SignupRequest signupRequest) {
        User user = new User();
        Password pwd = new Password();
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getUsername());
        if (signupRequest.getPassword() != null){
            pwd.setEmail(signupRequest.getEmail());
            pwd.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        }
//        user.setConnectCount(0);
//        user.setSocketId("");
        if ((!signupRequest.getEmail().isEmpty()) && (signupRequest.getEmail() != null) &&
                adminEmails.contains(signupRequest.getEmail()) == true) {
            user.setPermitLevel(10);
        } else user.setPermitLevel(1);
        try {
            userRepository.save(user);
            if (pwd.getPassword() != null) passwordRepository.save(pwd);
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
        userDto.setDormant(user.isDormant());
        return userDto;
    }

    public int getUserPermit(long id) {
        return userRepository.findUserById(id).getPermitLevel();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public long getIdByCookies(Cookie[] cookies) {
        String token = this.getParsedToken(cookies);
        return (this.getUserPrimaryKeyByJwt(token));
    }

    public User getUserByCookies(Cookie[] cookies) {
        long userPkId = this.getIdByCookies(cookies);
        return this.getUserById(userPkId);
    }

    public Map<String, String> getUserInfoMapByCookies(Cookie[] cookies) {
        String token = this.getParsedToken(cookies);
        return this.getUserInfoMapByJwt(token);
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

    public Map<String, String> getUserInfoMapByJwt(String token){
        if (token.isEmpty())
            throw new RuntimeException();

        Map<String, String> infoMap = new HashMap<>();
        String[] payload = authService.extractSubject(token).getSubject().split(",");

        for (String raw : payload){
            int separatorIndex = raw.indexOf("=");
            infoMap.put(raw.substring(0, separatorIndex), raw.substring(separatorIndex + 1));
        }

        return  infoMap;
    }

    public boolean isAdminLevelUser(Cookie[] cookies, int allowLevel){
        User user = this.getUserByCookies(cookies);
        return (user.getPermitLevel() >= allowLevel);
    }

    private String getParsedToken(Cookie[] cookies){
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

        return token;
    }

    public Set<String> getAdminEmails(){
        return this.adminEmails;
    }

    public void dormantUser(long id) {
        User user = userRepository.findUserById(id);
        user.setDormant(true);
        userRepository.save(user);
    }

    public void returningUser(long id) {
        User user = userRepository.findUserById(id);
        user.setDormant(false);
        userRepository.save(user);
    }

    public User getUserByEmailAndPwd(SignupRequest signupRequest) {
        try {
            Password pwd = passwordRepository.findByEmail(signupRequest.getEmail());
            if (pwd == null) return null;
            if (!passwordEncoder.matches(pwd.getPassword(), signupRequest.getPassword())) return null;
            return userRepository.findByEmail(signupRequest.getEmail());
        } catch (Exception e){
            return null;
        }
    }
}