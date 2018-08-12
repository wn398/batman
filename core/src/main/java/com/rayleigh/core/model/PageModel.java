package com.rayleigh.core.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
//@ApiModel("分页类型")
public class PageModel<T> {
    @ApiModelProperty("总记录数")
    private Long totalRecords;
    @ApiModelProperty("总页数")
    private Long totalPage;
    @ApiModelProperty("当前页数")
    private Integer currentPage;
    @ApiModelProperty("每页条数")
    private Integer pageSize;
    @ApiModelProperty("结果集")
    private List<T> results;

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
