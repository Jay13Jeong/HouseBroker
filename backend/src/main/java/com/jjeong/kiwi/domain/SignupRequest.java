package com.jjeong.kiwi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class  SignupRequest {
    private String username;
    private String email;
    private String password;
    private String emailcode;

}





