package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyClass {
    @GetMapping("/hello")
    public String hello()   {
        return "helloo";
    }

    @GetMapping("/final")
    public String ff()   {
        final User user = new User("jg","hg@naver.com","1234");
        System.out.println(user);
        user.setUsername("pong");
        System.out.println(user);
        return "helloo";
    }
}
