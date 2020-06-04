package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//原理：对于feign而言，只要容器中有拦截器，它就会自动从容器中获取拦截器
//所以我们只需要在容器中放一个拦截器就行
@Configuration
public class GuliFeignConfig {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //RequestContextHolder是上下文环境的保持器
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                    HttpServletRequest request = attributes.getRequest();
                    if(request!=null){
                        //同步请求头数据
                        String cookie = request.getHeader("Cookie");
                        template.header("Cookie",cookie);
                        System.out.println("feign远程之前先进行RequestInterceptor.apply");
                    }
                }


            }
        };
    }
}
