package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;
@Data
public class SearchResult {
    private List<SkuEsModel> product;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<BrandVo> brands;
    private List<AttrVo> attrs;
    private List<CatalogVo> catalogs;
    @Data
    public static class BrandVo {
        private Long brangId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

}
