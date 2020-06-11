package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SeckillSkuScheduled {
    //每天晚上三点，上架最近三天需要秒杀的商品
    @Autowired
    SeckillService seckillService;
    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";
    @Scheduled(cron = "*/10 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){
        log.info("上架秒杀的商品信息。。。");
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);

        try{
            seckillService.uploadSeckillSkuLatest3Days();
        }catch(Exception e){
           lock.unlock();
        }
    }
}
