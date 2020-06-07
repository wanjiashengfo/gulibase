package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService orderTaskService;
    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;
    @Autowired
    OrderFeignService orderFeignService;
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {

    }

    private void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId){
        wareSkuDao.unLockStock(skuId,wareId,num);
    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count==null?false:count>0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单详情，追溯
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);
        //1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            //查询这个商品在哪里有库存
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        Boolean allLock = true;
        //锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if(wareIds == null || wareIds.size()==0){
                //没有任何仓库有库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //不成功返回0
               Long count =  wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
               if(count == 1){

                   skuStocked = true;
                   WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null,skuId,"",hasStock.getNum(),taskEntity.getId(),wareId,1);
                   orderTaskDetailService.save(taskDetailEntity);
                   StockLockedTo lockedTo = new StockLockedTo();
                   lockedTo.setId(taskEntity.getId());
                   StockDetailTo stockDetailTo = new StockDetailTo();
                   BeanUtils.copyProperties(taskDetailEntity,stockDetailTo);
                   lockedTo.setDetail(stockDetailTo);
                   rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                   break;
               }else {
                   //当前仓库锁失败，重试下一个仓库
               }
            }
            if(skuStocked == false){
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }
        //肯定全部都是锁定成功的
        return true;

    }

    @Override
    public void unlockStock(StockLockedTo to) {

        Long id = to.getId();
        StockDetailTo detail = to.getDetail();
        Long skuId = detail.getSkuId();
        Long detailId = detail.getId();
        //解锁
        //查询数据库关于这个订单的锁定库存信息
        //如果没有 说明库存锁定失败，这种情况无需解锁
        //如果有
        //1.没有这个订单，则必须解锁
        //2.有这个订单 则看订单状态
        //  r如果是已取消，则解锁库存
        //   没取消 则不能解锁
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if(byId!=null){
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if(r.getCode()==0){
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if(data == null||data.getStatus() == 4){
                    //订单已经被取消了，才能解锁库存
                    unLockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                }
            }else {//消息解锁以后重新放入队列，让别人继续消费
            }
            //解锁
        }else {

        }
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}