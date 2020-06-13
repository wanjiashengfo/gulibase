package com.atguigu.common.exception;

import lombok.Data;
import lombok.Getter;


public enum BizCodeEnume {
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_SMS_CODE_EXCEPTION(10002,"验证码频率太高"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001,"用户存在"),
    LOGINACCT_PASSWORD_EXCEPTION(15003,"账号密码错误"),
    PHONE_EXIST_EXCEPTION(15002,"手机号存在"),
    NO_STOCK_EXECPTION(21000,"商品库存不足"),
    TOO_MANY_REQUEST(10777,"请求流量过大");
    @Getter
    private int code;
    @Getter
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
