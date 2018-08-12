package com.rayleigh.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//@ApiModel("间隔段model")
public class BetweenValue<T> {
    @ApiModelProperty("间隔段开始值")
    private T min;
    @ApiModelProperty("间隔段结束值")
    private T max;

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }
}
