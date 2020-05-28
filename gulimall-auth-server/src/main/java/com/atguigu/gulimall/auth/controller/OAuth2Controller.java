package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuth2Controller {
    @Autowired
    MemberFeignService memberFeignService;
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, ServletResponse servletResponse) throws Exception {
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
            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if(oauthlogin.getCode()==0){
                MemberResponseVo data = oauthlogin.getData("data", new TypeReference<MemberResponseVo>() {
                });
                //第一次使用session 命令浏览器保存 JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                //子域之间
                //发卡的时候（指定域名为父域名） 即使是子域发的卡 也能让父域直接使用
                session.setAttribute("loginUser",data);
                System.out.println("登陆成功：" + data);
                return "redirect:http://gulimall.com";
            }else {
                return "redirect:http://gulimall.com/login.html";
            }
        }else {
            return "redirect:http://gulimall.com/login.html";
        }

    }
}
