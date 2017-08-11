package com.rayleigh.core.enums;

/**
 * Created by wangn20 on 2017/6/13.
 * 包装结果类型   成功，失败，异常，验证失败
 */
public enum ResultStatus {
    SUCCESS("success"),
    FAILURE("failure"),
    EXCEPTION("exception"),
    NOT_VALID("not_valid");


    ResultStatus(String status){

    }


}
