package ${project.packageName}.standard.service;

import com.rayleigh.core.model.PageModel;
import ${project.packageName}.standard.model.${entity.name};
import com.rayleigh.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import ${project.packageName}.standard.methodModel.*;

/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
public interface ${entity.name}Service extends BaseService{

    List<${entity.name}> saveOrUpdate(List<${entity.name}> list);

    ${entity.name} saveWithAssignedId(${entity.name} ${entity.name ?uncap_first})throws Exception;

    ${entity.name} saveOrUpdate(${entity.name} ${entity.name ?uncap_first});

    void deleteByIds(List<String> ids);

    void deleteById(String id);

    List<${entity.name}> findByIds(List<String> ids);

    ${entity.name} findOne(String id);

    List<Object[]> listBySQL(String sql);

    Page<${entity.name}> findByAuto(${entity.name} ${entity.name ?uncap_first},Pageable pageable);

    Page<${entity.name}> findAll(Pageable pageable);

    List<${entity.name}> findAll(Specification<${entity.name}> specification);

    Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable);

    List<${entity.name}> findAll();

    <#list entity.methods as method>
    PageModel<${entity.name}$${method.methodName ?cap_first}ResultWrapper> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);

    </#list>
}

/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/