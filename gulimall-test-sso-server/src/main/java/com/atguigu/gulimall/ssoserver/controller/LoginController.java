package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller

public class LoginController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token){
        String s = stringRedisTemplate.opsForValue().get(token);
        return s;
    }

    @PostMapping("/doLogin")
    private String doLogin(@RequestParam("username") String name,
                           @RequestParam("password") String password,
                           @RequestParam("url") String url,
                           HttpServletResponse response){
        if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(password)){
            String uuid = UUID.randomUUID().toString().replace("-","");
            stringRedisTemplate.opsForValue().set(uuid,name);
            Cookie sso_token = new Cookie("sso_token",uuid);
            response.addCookie(sso_token);
            return "redirect:"+url+"?token="+uuid;
        }
        return "login";
    }
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url, Model model,
                            @CookieValue(value = "sso_token",required = false) String sso_token){
        if(!StringUtils.isEmpty(sso_token)){
            return "redirect:"+url+"?token="+sso_token;
        }
        model.addAttribute("url",url);
        return "login";
    }
}
