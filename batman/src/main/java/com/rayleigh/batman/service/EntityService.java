package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.core.service.BaseService;

import java.util.Date;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface EntityService {
    Entities save(Entities entities);

    Entities update(Entities entities);

    Entities partUpdate(Entities entities);

    Entities findOne(String id);

    void deleteOne(Entities entities);
    //获取实体属性里最大的更新时间
    Date getMaxFieldUpdateDateByEntityId(String entityId);
    //获取方法相关的最大更新时间
    Date getMaxMethodUpdateDateByEntityId(String entityId);
    //获取这个实体类下关系相关的最大时间
    Date getMaxRelationShipDateByEntityId(String entityId);


}
