package com.jjeong.kiwi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class DummyClass {
//    @GetMapping("/hello")
//    public String hello()   {
//        return "helloo";
//    }
//
//    @GetMapping("/oauth2/authorization/google")
//    public String dummyTest(HttpServletResponse response) {
//        System.out.println("dummyTest");
//        return "dummyTest";
//    }
//    @GetMapping("/error")
//    public RedirectView handleError(HttpServletRequest request) {
////
////        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
////        // error로 들어온 에러의 status를 불러온다 (ex:404,500,403...)
////
////        if(status != null){
////            int statusCode = Integer.valueOf(status.toString());
////
////            if(statusCode == HttpStatus.NOT_FOUND.value()) {
////                return new RedirectView("/");
////            } else {
////                return new RedirectView("/");
////            }
////        }
//        return new RedirectView("/");
//    }
}
