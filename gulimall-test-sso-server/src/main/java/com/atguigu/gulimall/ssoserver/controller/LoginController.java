package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class LoginController {
    private String doLogin(){
        return "";
    }
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url){
        return "login";
    }
}
