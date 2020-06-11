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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    public String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        //确定当前时间是哪个秒杀场次


        long time = new Date().getTime();
        String s1 = transferLongToDate("yyyy-MM-dd HH:mm:ss", time);
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            //seckill:sessions:1591941600000_1591948800000
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if(time>=start && time<=end){
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);
                if(list!=null){
                    List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                        SecKillSkuRedisTo redis = JSON.parseObject(item.toString(), SecKillSkuRedisTo.class);
                        //当前秒杀已经开始，需要随机码
//                        redis.setRandomCode(null);
                        return redis;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }

        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session->{
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX+startTime+"_"+endTime;
            //缓存活动信息
            Boolean hasKey = redisTemplate.hasKey(key);
            if(!hasKey){
                List<String> collect = session.getRelationSkus().stream().map(item->item.getPromotionSessionId().toString()+"_"+item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,collect);
            }
        });
    }
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session -> {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);//绑定hash操作
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //随机码
                String token = UUID.randomUUID().toString().replace("-", "");//随机码
//                token = UUID.randomUUID().toString().replace("-", "");
                if(!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString())) {
                    //缓存商品
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                    //sku基本数据
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(info);
                    }
                    //sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    //设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    redisTo.setRandomCode(token);

                    String jsonString = JSON.toJSONString(redisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString(), jsonString);

                    //引入分布式的信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }

            });
        });
    }
}
