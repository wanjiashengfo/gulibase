package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author liuchenxi
 * @email liuchenxi938@gmail.com
 * @date 2020-05-06 13:07:07
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
