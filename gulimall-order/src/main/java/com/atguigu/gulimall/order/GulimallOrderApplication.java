package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableRabbit
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
