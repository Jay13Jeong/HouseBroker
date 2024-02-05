package com.jjeong.kiwi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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





