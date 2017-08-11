package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DateBetweenValue {
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date min;
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
        if(null !=null){
            return (Date)max.clone();
        }else{
            return null;
        }
    }

    public void setMax(Date max) {
        this.max = (Date)max.clone();
    }
}
