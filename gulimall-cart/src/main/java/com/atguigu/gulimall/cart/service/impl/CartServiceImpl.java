package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = new CartItem();
        //远程查询商品信息
        CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
            R skuInfo = productFeignService.getSkuInfo(skuId);
            SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });

            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(data.getSkuDefaultImg());
            cartItem.setTitle(data.getSkuTitle());
            cartItem.setSkuId(data.getSkuId());
            cartItem.setPrice(data.getPrice());
        },executor);
        //远程查询sku组合信息
        CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
            List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(values);
        }, executor);
        CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
        return cartItem;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId()!=null){
            cartKey = CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey = CART_PREFIX+userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);

        return operations;
    }
}
