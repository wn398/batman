package com.rayleigh.batman.util;

import org.mapstruct.factory.Mappers;

/**
 * Created by wangn20 on 2017/7/4.已经弃用
 */
public class MapperUtil {
    public static ModelMapper getModelMapper(){
        return Mappers.getMapper(ModelMapper.class);
    }

}
