package com.atguigu.common.exception;

import lombok.Data;

@Data
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品"+skuId+"没有足够的库存");
    }
}
