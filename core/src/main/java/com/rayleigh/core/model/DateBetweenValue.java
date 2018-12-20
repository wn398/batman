package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
//@ApiModel("日期间隔model")
public class DateBetweenValue {
    @ApiModelProperty(value = "开始时间",example = "1997-05-04 12:12:12")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date min;

    @ApiModelProperty(value = "结束时间",example = "1997-05-04 12:12:12")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date max;

    public Date getMin() {
        if(null != min){
            return (Date)min.clone();
        }else{
            return null;
        }
    }

    public void setMin(Date min) {
        this.min = (Date)min.clone();
    }

    public Date getMax() {
        if(null !=max){
            return (Date)max.clone();
        }else{
            return null;
        }
    }

    public void setMax(Date max) {
        this.max = (Date)max.clone();
    }
}
