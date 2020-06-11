package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SecKillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.atguigu.gulimall.seckill.vo.SeckillSkuVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //扫描需要参与秒杀的活动
        R session = couponFeignService.getLates3DaySession();
        if(session.getCode()==0){
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            //缓存到redis
            //缓存活动信息
            saveSessionInfos(sessionData);
            //缓存活动的关联信息
            saveSessionSkuInfos(sessionData);
        }
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session->{
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX+startTime+"_"+endTime;
            List<String> collect = session.getRelationSkus().stream().map(item->item.getSkuId().toString()).collect(Collectors.toList());
            //缓存活动信息
            redisTemplate.opsForList().leftPushAll(key,collect);
        });
    }
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session -> {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);//绑定hash操作
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //缓存商品
                SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                //sku基本数据
                R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                if(skuInfo.getCode()==0){
                    SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    redisTo.setSkuInfo(info);
                }
                //sku秒杀信息
                BeanUtils.copyProperties(seckillSkuVo,redisTo);
                //设置当前商品的秒杀时间信息
                redisTo.setStartTime(session.getStartTime().getTime());
                redisTo.setEndTime(session.getEndTime().getTime());
                //随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                redisTo.setRandomCode(token);
                //引入分布式的信号量
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                //商品可以秒杀的数量作为信号量
                semaphore.trySetPermits(seckillSkuVo.getSeckillCount());

                String jsonString = JSON.toJSONString(redisTo);
                ops.put(seckillSkuVo.getSkuId().toString(),jsonString);
            });
        });
    }
}
