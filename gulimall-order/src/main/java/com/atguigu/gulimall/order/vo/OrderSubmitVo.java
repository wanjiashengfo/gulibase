package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;

    private String orderToken;//防重令牌
    private BigDecimal payPrice;//应付价格
    private String note;
}
