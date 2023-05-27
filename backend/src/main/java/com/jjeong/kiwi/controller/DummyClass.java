package com.jjeong.kiwi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyClass {
    @GetMapping("/hello")
    public String hello()   {
        return "helloo";
    }
}
