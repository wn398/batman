package com.rayleigh.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rayleigh.core.annotation.FieldInfo;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangn20 on 2017/6/12.
 */
//@JSONType(ignores={"logger"})
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @FieldInfo("主键")
    @Column(length = 48,nullable = false)
    @Size(max=48, min=1, message = "主键ID 长度必须大于等于1且小于等于48")
    public String id;

    @FieldInfo("创建时间")
    @ApiModelProperty(hidden=true)
    @CreatedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date createDate;

    @FieldInfo("更新时间")
    @ApiModelProperty(hidden=true)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(nullable = false)
    public Date updateDate;

    @FieldInfo("版本号")
    @Version
    public Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
