package com.jjeong.kiwi.controller;

import com.jjeong.kiwi.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class DummyClass {
    @GetMapping("/hello")
    public String hello()   {
        return "helloo";
    }

    @GetMapping("/oauth2/authorization/google")
    public String dummyTest(HttpServletResponse response) {
        System.out.println("dummyTest");
        return "dummyTest";
    }

}
