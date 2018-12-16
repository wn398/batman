package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rayleigh.core.enums.ResultStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangn20 on 2017/7/4.
 */
//@ApiModel("统一返回结果包装类")
public class ResultWrapper<T> {

    @NotEmpty
    @ApiModelProperty("状态")
    private ResultStatus status;

    @ApiModelProperty("结果数据")
    private T data;

    @ApiModelProperty("系统验证信息")
    private Map<String,Object> validMessage = new HashMap<>();

    @ApiModelProperty("异常信息")
    private String exceptionMessage;

    @ApiModelProperty("提示信息")
    private String info;

    @ApiModelProperty("业务code")
    private String code;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("时间")
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
