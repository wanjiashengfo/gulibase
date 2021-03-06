package com.atguigu.gulimall.auth;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallAuthServerApplicationTests {

    @Test
    public void contextLoads() throws Exception {
        Map<String,String> header = new HashMap<>();
        Map<String,String> query = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("client_id","2523866155");
        map.put("client_secret","5a997fa23c8e36a6faff8f6142dfce59");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code","be8f0f9c17328e8ceac076b0b512499e");
        //根据code换取accessToken
        HttpResponse response = HttpUtils.doPost("http://api.weibo.com", "/oauth2/access_token", "post",header,  query, map);
        if(response.getStatusLine().getStatusCode()==200){
            //获取到了accesstoken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

        }
    }

}
