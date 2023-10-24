package com.jjeong.kiwi.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebErrorController implements ErrorController {
//    @Override
//    public String getErrorPath() {
//        return null;
//    }

    @GetMapping("/error")
    public RedirectView handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if(status != null){
//            int statusCode = Integer.valueOf(status.toString());
//
//            if(statusCode == HttpStatus.NOT_FOUND.value()) {
//                return new RedirectView("/");
//            } else {
//                return new RedirectView("/");
//            }
//        }
        return new RedirectView("/");
    }

}
