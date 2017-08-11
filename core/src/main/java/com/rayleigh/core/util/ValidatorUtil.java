package com.rayleigh.core.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangn20 on 2017/7/13.
 */
//hibernate validator验证信息支持
public class ValidatorUtil {
    //获取验证器
    public static Validator getValidator(){
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
    //解析错误
    public Map parseResult(Set<ConstraintViolation> constraintViolationSet){
        for(ConstraintViolation constraintViolation:constraintViolationSet){

            constraintViolation.getMessage();
        }
        return null;
    }
}
