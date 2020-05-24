package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class SkuItemVo {
    SkuInfoEntity info;
    List<SkuImagesEntity> images;
    SpuInfoDescEntity desp;
    List<SkuItemSaleAttrVo> saleAttr;
    List<SpuItemAttrGroupVo> groupAttrs;
    boolean hasStock = true;

}
