package com.rayleigh.batman.util;

import com.rayleigh.batman.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Created by wangn20 on 2017/7/4.
 */
@Mapper(nullValueMappingStrategy= NullValueMappingStrategy.RETURN_DEFAULT,nullValueCheckStrategy= NullValueCheckStrategy.ALWAYS)
public interface ModelMapper {
    //copy属性，全部copy，为空也copy
    Project copyPropertyForProject (Project source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    Project updatePropertyForProject(Project source, @MappingTarget Project target);

    //copy属性，全部copy，为空也copy
    Entities copyPropertyForEntities (Entities source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    Entities updatePropertyForEntities(Entities source, @MappingTarget Entities target);

    //copy属性，全部copy，为空也copy
    Field copyPropertyForField (Field source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    Field updatePropertyForField(Field source, @MappingTarget Field target);

    //copy属性，全部copy，为空也copy
    Module copyPropertyForModule (Module source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    Module updatePropertyForModule(Module source, @MappingTarget Module target);

    //copy属性，全部copy，为空也copy
    RelationShip copyPropertyForRelationShip (RelationShip source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    RelationShip updatePropertyForRelationShip(RelationShip source, @MappingTarget RelationShip target);

    //copy属性，全部copy，为空也copy
    SysUser copyPropertyForSysUsert (SysUser source);

    //更新目标属性,会做空检查，如果源属性为null,则保留目标属性的值，有就更新，没有就不动
    SysUser updatePropertyForSysUser(SysUser source, @MappingTarget SysUser target);

}
