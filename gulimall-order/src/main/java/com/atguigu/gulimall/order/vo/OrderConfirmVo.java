package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class OrderConfirmVo {
    @Setter @Getter
    List<MemberAddressVo> address;
    @Setter @Getter
    List<OrderItemVo> items;
    @Setter @Getter
    Integer intergration;

    BigDecimal total;

    BigDecimal payPrice;
    @Setter @Getter
    String orderToken;
    @Setter @Getter
    Map<Long,Boolean> stocks;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
        }

        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public Integer getCount(){
        Integer i = 0;
        if(items!=null){
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }
}
