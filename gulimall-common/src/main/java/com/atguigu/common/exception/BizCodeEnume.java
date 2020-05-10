package com.atguigu.common.exception;

import lombok.Data;
import lombok.Getter;


public enum BizCodeEnume {
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    UNKNOW_EXCEPTION(10000,"系统未知异常");
    @Getter
    private int code;
    @Getter
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
