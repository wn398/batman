package com.rayleigh.batman.uiModel;

import com.rayleigh.batman.model.Entities;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel("数据库连接信息及此连接下表信息model")
public class DataBaseConnectionWithEntityModel extends DataBaseConnectionModel{
    private List<Entities> entity;

    public List<Entities> getEntity() {
        return entity;
    }

    public void setEntity(List<Entities> entity) {
        this.entity = entity;
    }
}
