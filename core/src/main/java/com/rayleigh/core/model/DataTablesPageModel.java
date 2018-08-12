package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by wangn20 on 2017/7/5.
 */
//@ApiModel("dataTable分页插件model")
public class DataTablesPageModel {
    @ApiModelProperty("起始页码")
    private int start;
    @ApiModelProperty("每页条数")
    private int pageSize;

    @ApiModelProperty("dataTable特殊用，重绘次数")
    private int draw;

    @ApiModelProperty("总记录条数")
    private Long recordsTotal;

    @ApiModelProperty("查询过滤出来的条数")
    private Long recordsFiltered;
    @ApiModelProperty("数据")
    private Object data;

    @ApiModelProperty("存放错误信息")
    private String error;

    @ApiModelProperty("回复时间，请求时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    public Date getDateTime() {
        if(null!=dateTime) {
            return (Date) dateTime.clone();
        }else{
            return null;
        }
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = (Date)dateTime.clone();
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
