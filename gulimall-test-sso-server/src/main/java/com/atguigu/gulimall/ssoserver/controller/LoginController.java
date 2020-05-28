package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller

public class LoginController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @PostMapping("/doLogin")
    private String doLogin(@RequestParam("username") String name,@RequestParam("password") String password,@RequestParam("url") String url){
        if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(password)){
            String uuid = UUID.randomUUID().toString().replace("-","");
            stringRedisTemplate.opsForValue().set(uuid,name);
            return "redirect:"+url+"?token="+uuid;
        }
        return "login";
    }
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url, Model model){
        model.addAttribute("url",url);
        return "login";
    }
}
