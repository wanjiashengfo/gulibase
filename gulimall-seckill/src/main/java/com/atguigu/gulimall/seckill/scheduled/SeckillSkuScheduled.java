package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeckillSkuScheduled {
    //每天晚上三点，上架最近三天需要秒杀的商品
    @Autowired
    SeckillService seckillService;
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){
        seckillService.uploadSeckillSkuLatest3Days();
    }
}
