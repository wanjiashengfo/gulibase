package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuth2Controller {
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code) throws Exception {
        Map<String,String> query = new HashMap<>();
        Map<String,String> header = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("client_id","2523866155");
        map.put("client_secret","5a997fa23c8e36a6faff8f6142dfce59");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        //根据code换取accessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post",header,  query, map);
        if(response.getStatusLine().getStatusCode()==200){
            //获取到了accesstoken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

        }
        return "redirect:http://gulimall.com";
    }
}
