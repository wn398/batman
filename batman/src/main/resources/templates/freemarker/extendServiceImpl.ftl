<#include "CopyRight.ftl">
package ${project.packageName}.extend.service.impl;

import ${project.packageName}.base.model.${entity.name};
import ${project.packageName}.extend.repository.${entity.name}ExtendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${project.packageName}.base.service.impl.${entity.name}ServiceImpl;
import ${project.packageName}.extend.service.${entity.name}ExtendService;

@Service
public class ${entity.name}ExtendServiceImpl extends ${entity.name}ServiceImpl implements ${entity.name}ExtendService {
@Autowired
private ${entity.name}ExtendRepository ${entity.name ?uncap_first}ExtendRepository;



}
