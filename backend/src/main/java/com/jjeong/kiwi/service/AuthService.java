package com.jjeong.kiwi.service;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.jjeong.kiwi.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import java.util.Date;

//import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.REDIRECT_URI;
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String SECRET_KEY = System.getenv("JWTKEY");
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    private static final String CLIENT_ID = System.getenv("GOOGLE_AUTH_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_ACCESS_SECRET");
    private static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";
    private static final String REDIRECT_URI = System.getenv("GOOGLE_AUTH_CALLBACK_URL");
    private final String authorizationServerUrl = "https://accounts.google.com/o/oauth2/auth";
    private final String scopes = "profile email";

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String email = user.getEmail();
        String authId = user.getAuthid();

        // JWT 토큰에 포함될 클레임(claim)을 구성합니다.
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("authId", authId);

        return Jwts.builder()
                .setSubject(claims.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String extractSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public GoogleIdToken parseIdToken(String idToken) throws GeneralSecurityException, IOException {
        // transport 객체 생성
        HttpTransport transport = new NetHttpTransport();

        // jsonFactory 객체 생성
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        return googleIdToken;
    }

    public GoogleTokenResponse exchangeAuthorizationCode(String authorizationCode) throws GeneralSecurityException, IOException {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GenericUrl tokenServerUrl = new GenericUrl(TOKEN_SERVER_URL);

        AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(
                transport, jsonFactory, tokenServerUrl, CLIENT_ID);

        TokenResponse tokenResponse = tokenRequest.setRedirectUri(REDIRECT_URI)
                .setCode(authorizationCode)
                .execute();

        return new GoogleTokenResponse().setAccessToken(tokenResponse.getAccessToken())
                .setExpiresInSeconds(tokenResponse.getExpiresInSeconds())
                .setIdToken(tokenResponse.get("id_token").toString());
    }
}
