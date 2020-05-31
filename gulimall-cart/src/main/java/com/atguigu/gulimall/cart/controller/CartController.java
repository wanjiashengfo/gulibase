package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
    @Autowired
    CartService cartService;
    /**
     * 浏览器有一个cookie：user-key 标识用户身份 一个月之后过期
     * 如果第一次使用购物车 都会给一个临时的用户身份
     * 每次访问都会带上这个cookie
     *
     *
     * @param session
     * @return
     */
   @GetMapping("/cart.html")
    public String cartListPage(HttpSession session){
       UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
       System.out.println(userInfoTo);
       return "cartList";
   }
   @GetMapping("/addToCart")
   public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) throws ExecutionException, InterruptedException {
       CartItem cartItem = cartService.addToCart(skuId,num);
       model.addAttribute("item",cartItem);
        return "success";
   }
}