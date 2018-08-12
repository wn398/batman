package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rayleigh.core.annotation.FieldInfo;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangn20 on 2017/6/12.
 */
//@JSONType(ignores={"logger"})
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseModel2 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    public Long id;

    @ApiModelProperty(value = "创建时间",hidden=true)
    @CreatedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date createDate;

    @ApiModelProperty(value = "更新时间",hidden=true)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(nullable = false)
    public Date updateDate;

    @ApiModelProperty("版本号")
    @Version
    public Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        if(null!=createDate) {
            return (Date) createDate.clone();
        }else{
            return null;
        }
    }

    public void setCreateDate(Date createDate) {
        this.createDate = (Date)createDate.clone();
    }

    public Date getUpdateDate() {
        if(null!=updateDate) {
            return (Date) updateDate.clone();
        }else{
            return null;
        }
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = (Date)updateDate.clone();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public static void main(String[] args){


    }
}
