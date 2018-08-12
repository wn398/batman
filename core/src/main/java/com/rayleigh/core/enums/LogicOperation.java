package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;

@ApiModel("逻辑操作模型")
public enum LogicOperation {
    or("or"),
    and("and");
    LogicOperation(String name){}
}
