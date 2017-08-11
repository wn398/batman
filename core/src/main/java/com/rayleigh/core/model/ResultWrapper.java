package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rayleigh.core.enums.ResultStatus;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangn20 on 2017/7/4.
 */
//结果包装类
public class ResultWrapper {

    //状态
    @NotEmpty
    private ResultStatus status;

    //存放结果数据
    private Object data;

    //用于验证的错误信息
    private Map<String,Object> validMessage = new HashMap<>();

    //存放错误信息
    private String exceptionMessage;
    //提示信息
    private String info;
    //时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dateTime = new Date();

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, Object> getValidMessage() {
        return validMessage;
    }

    public void setValidMessage(Map<String, Object> validMessage) {
        this.validMessage = validMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Date getDateTime() {
        return (Date)dateTime.clone();
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = (Date)dateTime.clone();
    }
}
