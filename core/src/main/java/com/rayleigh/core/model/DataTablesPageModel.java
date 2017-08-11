package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by wangn20 on 2017/7/5.
 */
//用于分页的model
public class DataTablesPageModel {
    private int start;
    private int pageSize;
    //dataTable特殊用,重绘次数
    private int draw;
    //总记录数
    private Long recordsTotal;
    //查询过滤出来的条数
    private Long recordsFiltered;
    private Object data;
    //存放错误信息
    private String error;
    //回复时间,请求时间
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
