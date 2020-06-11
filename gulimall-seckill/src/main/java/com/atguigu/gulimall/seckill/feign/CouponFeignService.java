package com.atguigu.gulimall.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
}
