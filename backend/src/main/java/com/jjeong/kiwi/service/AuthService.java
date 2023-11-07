package com.jjeong.kiwi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjeong.kiwi.domain.SignupRequest;
import com.jjeong.kiwi.domain.User;
import com.jjeong.kiwi.domain.UserInfoResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    private static final String CLIENT_ID = System.getenv("GOOGLE_AUTH_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_ACCESS_SECRET");
    private static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";
    private static final String REDIRECT_URI = System.getenv("GOOGLE_AUTH_CALLBACK_URL");
    private static final String CONFIRM_MAIL_TITLE = System.getenv("CONFIRM_MAIL_TITLE");

    private static final Map<String, List> emailAuthList = new HashMap<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender javaMailSender;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String email = user.getEmail();
        String authId = user.getAuthid();
        String name = user.getUsername();

//        System.out.println("generateToken ===========");
//        System.out.println(user);
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("authId", authId);
        claims.put("name", name);
        claims.put("id",user.getId());

        return Jwts.builder()
                .setSubject(claims.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims extractSubject(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);

        return jws.getBody();
    }

    public User getInfoByCode(String authorizationCode) {
        String accessTokenUrl = TOKEN_SERVER_URL;
        String clientId = CLIENT_ID;
        String clientSecret = CLIENT_SECRET;
        String redirectUri = REDIRECT_URI;
        String grantType = "authorization_code";

        // access token 요청을 위한 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", grantType);

//        System.out.println("InfoByCode part 1 =======");

        // access token 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, requestEntity, String.class);

//        System.out.println(responseEntity.getBody());
//        System.out.println("InfoByCode part 2 =======");

        // access token과 함께 이메일 및 아이디 가져오기
        String accessToken = responseEntity.getBody();
        User user = this.getUserInfo(accessToken);

//        System.out.println("InfoByCode part 3 =======");

        return user;
    }

    private User getUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";

        // 이메일 및 아이디 요청을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();

        String accessTokenValue = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(accessToken);
            accessTokenValue = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        if (accessTokenValue == null) {
            // 유효한 토큰 추출 실패
            return null;
        }

        headers.setBearerAuth(accessTokenValue);

//        System.out.println("EmailAndUserId part 1 =======");
//        System.out.println(headers);

        // 이메일 및 아이디 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<UserInfoResponse> responseEntity = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                requestEntity,
                UserInfoResponse.class
        );
        UserInfoResponse userInfoResponse = responseEntity.getBody();

//        System.out.println("EmailAndUserId part 2 =======");
//        System.out.println(responseEntity.getBody());


        User user = new User();
        user.setAuthid(userInfoResponse.getId());
        user.setUsername(userInfoResponse.getName());
        user.setEmail(userInfoResponse.getEmail());

        return user;
    }

    public boolean sendConfirmMail(String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        List<String> codeAndCreateTime = new ArrayList<>();
        String code = UUID.randomUUID().toString().substring(0,5);
        Date currentDate = new Date();
        String createTime = sdf.format(currentDate);
//        Date parsedDate = sdf.parse(dateStr);
        codeAndCreateTime.add(code);
        codeAndCreateTime.add(createTime);
        emailAuthList.put(email, codeAndCreateTime);
        try {
            helper.setTo(email);
            helper.setSubject(CONFIRM_MAIL_TITLE);
            helper.setText(code, true);
            javaMailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmEmail(SignupRequest signupRequest) {
        try {
            List<String> codeAndCreateTime = emailAuthList.get(signupRequest.getEmail());
            String code = codeAndCreateTime.get(0);
            Date currentDate = new Date();
            String createTime = codeAndCreateTime.get(1);
            Date parsedDate = sdf.parse(createTime);
            long timeDifferenceMillis = Math.abs(currentDate.getTime() - parsedDate.getTime());
            long minutesDifference = timeDifferenceMillis / (60 * 1000);
            if (minutesDifference >= 5) { //5분경과하면 실패.
                emailAuthList.remove(signupRequest.getEmail());
                return false;
            }
            if (!code.equals(signupRequest.getEmailcode()) ){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = 60000) // 60 seconds
    private void removeOldEmailcode() {
        Date currentDate = new Date();
        for (Map.Entry<String, List> entry : emailAuthList.entrySet()) {
            String key = entry.getKey();
            try {
                List<String> value = entry.getValue();
                String createTime = value.get(1);
                Date parsedDate = sdf.parse(createTime);
                long timeDifferenceMillis = Math.abs(currentDate.getTime() - parsedDate.getTime());
                long minutesDifference = timeDifferenceMillis / (60 * 1000);
                if (minutesDifference >= 5) { //5분경과하면 제거.
                    emailAuthList.remove(key);
                }
            } catch (Exception e){
                emailAuthList.remove((key));
            }
        }
    }
}
