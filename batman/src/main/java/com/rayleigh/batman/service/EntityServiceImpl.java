package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.repository.EntitiesRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.util.BaseModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class EntityServiceImpl implements EntityService {
    //public Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EntitiesRepository entityRepository;
    @Autowired
    private ModuleService moduleService;

    @Override
    public Entities save(Entities entities) {
        return entityRepository.save(entities);
    }

    @Override
    public Entities update(Entities entities) {
        return entityRepository.save(entities);
    }

    @Override
    public Entities partUpdate(Entities entities) {
//        Entities databaseEntities = entityRepository.findOne(entities.getId());
//        BaseModelUtil.copyProperty(entities,databaseEntities);

        Entities resultEntities = (Entities) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(entities);
        //MapperUtil.getModelMapper().updatePropertyForEntities(entities,databaseEntities);
        entityRepository.save(resultEntities);
        return resultEntities;
    }

    @Override
    public Entities findOne(String id) {
        return entityRepository.findOne(id);
    }

    @Override
    public void deleteOne(Entities entities) {
        entityRepository.delete(entities.getId());
    }

    @Override
    public Date getMaxFieldUpdateDateByEntityId(String entityId) {
        return entityRepository.getMaxFieldUpdateDateByEntityId(entityId);
    }
    //获取方法相关
    @Override
    public Date getMaxMethodUpdateDateByEntityId(String entityId) {
        List<List<Date>> list = entityRepository.getMaxMethodUpdateDateByEntityId(entityId);
        if(null!=list &&list.size()>0) {
            if(null!=list.get(0) && list.get(0).size()>0) {
                List<Date> result =list.get(0).stream().filter(it->null!=it).collect(Collectors.toList());
                if(null!=result && result.size()>0) {
                    return Collections.max(result);
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    //获取关系相关的最大时间
    @Override
    public Date getMaxRelationShipDateByEntityId(String entityId) {
        List<List<Date>> list = entityRepository.getMaxRelationShipUpdateDateByEntityId(entityId);
        List<List<Date>> list2 = entityRepository.getMaxFieldRelationShipUpdateDateByEntityId(entityId);
        List<Date> dateList = new ArrayList<>();
        if(null!=list && list.size()>0){
            if(null!=list.get(0)&&list.get(0).size()>0) {
                List<Date> result =list.get(0).stream().filter(it->null!=it).collect(Collectors.toList());
                dateList.addAll(result);
            }
        }
        if(null!=list2 && list2.size()>0){
            if(null!=list2.get(0)&&list2.get(0).size()>0) {
                List<Date> result =list2.get(0).stream().filter(it->null!=it).collect(Collectors.toList());
                dateList.addAll(result);
            }
        }
        if(dateList.size()>0){
            return Collections.max(dateList);
        }
        return null;
    }


}
