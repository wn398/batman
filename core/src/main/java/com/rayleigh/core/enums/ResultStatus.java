package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangn20 on 2017/6/13.
 * 包装结果类型   成功，失败，异常，验证失败
 */
@ApiModel("结果状态枚举")
public enum ResultStatus {
    @ApiModelProperty("成功")
    SUCCESS("success"),
    @ApiModelProperty("失败")
    FAILURE("failure"),
    @ApiModelProperty("发生异常")
    EXCEPTION("exception"),
    @ApiModelProperty("没有通过验证")
    NOT_VALID("not_valid"),
    @ApiModelProperty("token验证没通过")
    INVALID_TOKEN("invalid_token"),
    @ApiModelProperty("token过期")
    EXPIRED_TOKEN("expired_token");


    ResultStatus(String status){

    }


}
