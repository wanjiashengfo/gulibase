package com.atguigu.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberWebController {
    @GetMapping("/memberOrder.html")
    public String memberOrderPage(){
        return "orderList";
    }
}
