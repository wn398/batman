package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangn20 on 2017/6/14.
 */
@ApiModel("映射模型")
public enum MappingDirection {
    @ApiModelProperty("单向")
    OneWay,
    @ApiModelProperty("双向")
    BothWay
}
