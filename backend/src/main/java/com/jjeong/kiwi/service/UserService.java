package com.jjeong.kiwi.service;

import com.jjeong.kiwi.model.Password;
import com.jjeong.kiwi.dto.SignupRequest;
import com.jjeong.kiwi.model.User;
import com.jjeong.kiwi.dto.UserDto;
import com.jjeong.kiwi.repository.PasswordRepository;
import com.jjeong.kiwi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static Set<String> adminEmails = new HashSet<>();
    static {
        String mailList = System.getenv("ADMIN_EMAIL");
        if (mailList != null && !mailList.isEmpty()){
            for(String email : mailList.split(",")){
                adminEmails.add(email);
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public boolean createUser(SignupRequest signupRequest, boolean isNomalSignup) {
        User user = new User();
        Password pwd = null;
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getUsername());
        if (isNomalSignup && (signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty())){
            return false;
        }
        if (isNomalSignup){
            pwd = passwordRepository.findByEmail(signupRequest.getEmail());
            if (pwd == null) pwd = new Password();
            pwd.setEmail(signupRequest.getEmail());
            pwd.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        }
        if ((!signupRequest.getEmail().isEmpty()) && (signupRequest.getEmail() != null) &&
                adminEmails.contains(signupRequest.getEmail()) == true) {
            user.setPermitLevel(10);
        } else user.setPermitLevel(1);
        try {
            userRepository.save(user);
            if (isNomalSignup && pwd != null) passwordRepository.save(pwd);
            return true;
        } catch (Exception e) {
            logger.error("createUser", e);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    public User getUserByAuthId(String authId) {
//        return userRepository.findUserByAuthid(authId);
//    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findUserById(id);
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername((user.getUsername()));
        userDto.setDormant(user.isDormant());
        return userDto;
    }

    @Transactional(readOnly = true)
    public int getUserPermit(long id) {
        return userRepository.findUserById(id).getPermitLevel();
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public long getIdByCookies(Cookie[] cookies) {
        String token = this.getParsedToken(cookies);
        return (this.getUserPrimaryKeyByJwt(token));
    }

    public User getUserByCookies(Cookie[] cookies) {
        String token = this.getParsedToken(cookies);
        if (token.isEmpty())
            throw new RuntimeException("404 getUserPrimaryKeyByJwt : ");
        return (this.getUserByJwt(token));
    }

    private String getValueByKey(String payload, String key){
        String value;
        int startIndex = payload.indexOf(key + "=") + key.length() + 1;  // "id=" 다음 문자의 인덱스를 구합니다.
        int endIndex = payload.indexOf(",", startIndex);  // 쉼표 이전의 인덱스를 구합니다.

        if (endIndex == -1)
            endIndex = payload.length() - 1;
        try {
            value = payload.substring(startIndex, endIndex);
        } catch (Exception e){
            logger.info("getValueByKey: invalid key-value", e);
            throw new RuntimeException("getValueByKey: invalid key-value");
        }
        return  value;
    }

    private User getUserByJwt(String token) {
        User user = new User();

        String payload = authService.extractSubject(token).getSubject();
        user.setId(Long.parseLong(getValueByKey(payload, "id")));
        user.setDormant(Boolean.parseBoolean(getValueByKey(payload, "dormant")));
        user.setPermitLevel(Integer.parseInt(getValueByKey(payload, "permitLevel")));
        user.setEmail(getValueByKey(payload, "email"));
        user.setUsername(getValueByKey(payload, "name"));

        return  user;
    }

    public Map<String, String> getUserInfoMapByCookies(Cookie[] cookies) {
        String token = this.getParsedToken(cookies);
        return this.getUserInfoMapByJwt(token);
    }

    public long getUserPrimaryKeyByJwt(String token){
        if (token.isEmpty())
            throw new RuntimeException("404 getUserPrimaryKeyByJwt : ");

        String payload = authService.extractSubject(token).getSubject();

        long id = 0;
        int startIndex = payload.indexOf("id=") + 3;  // "id=" 다음 문자의 인덱스를 구합니다.
        int endIndex = payload.indexOf(",", startIndex);  // 쉼표 이전의 인덱스를 구합니다.

        if (endIndex == -1)
            endIndex = payload.length() - 1;

        id = Long.parseLong(payload.substring(startIndex, endIndex));

        if (id == 0) {
            logger.error("getUserPrimaryKeyByJwt:user not found");
            throw new RuntimeException();
        }
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
            logger.error("getParsedToken:user not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "getParsedToken");
        }
        return token;
    }

    public Set<String> getAdminEmails(){
        return this.adminEmails;
    }

    @Transactional
    public void dormantUser(long id) {
        User user = userRepository.findUserById(id);
        user.setDormant(true);
        userRepository.save(user);
    }

    @Transactional
    public void returningUser(long id) {
        User user = userRepository.findUserById(id);
        user.setDormant(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmailAndPwd(SignupRequest signupRequest) {
        try {
            Password pwd = passwordRepository.findByEmail(signupRequest.getEmail());
            if (pwd == null) return null;
            if (!passwordEncoder.matches(signupRequest.getPassword(), pwd.getPassword())) return null;
            return userRepository.findByEmail(signupRequest.getEmail());
        } catch (Exception e){
            logger.error("getUserByEmailAndPwd", e);
            return null;
        }
    }
}