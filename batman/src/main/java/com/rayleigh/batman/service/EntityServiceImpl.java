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


}
