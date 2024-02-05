package com.jjeong.kiwi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class UserInfoResponse {
    private String email;
    private String id;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}
