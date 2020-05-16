package com.atguigu.common.constant;

import lombok.Getter;

public class ProductConstant {
    @Getter
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;
    }
    @Getter
    public enum StatusEnum{
        NEW_SPU(0,"新建"),SPU_UP(1,"上架"),SPU_DOWN(2,"下架");
        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;
    }
}
