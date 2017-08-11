package com.rayleigh.core.config;

import com.alibaba.fastjson.JSONObject;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by wangn20 on 2017/7/11.
 */
//spring_mvc层全局异常捕获
@ControllerAdvice
public class GlobleExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobleExceptionHandler.class);

    //应用上下文中设置ctx是，对所有controller适用
//    @ModelAttribute(value="ctx")
//    public String setContextPath(HttpServletRequest request){
//        return request.getContextPath();
//    }
    //拦截请求参数验证不通过的请求，封装后返回友好的格式
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultWrapper handleIOException(HttpServletRequest request, HttpServletResponse response, Model model, MethodArgumentNotValidException e) {

        log(e, request);
        //重新封装需要返回的错误信息
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.NOT_VALID);
        Map<String, Object> map = resultWrapper.getValidMessage();
        e.getBindingResult().getFieldErrors().parallelStream().forEach(fieldError ->
                map.put(new StringBuilder(fieldError.getObjectName()).append(".").append(fieldError.getField()).toString(),
                        new StringBuilder(fieldError.getDefaultMessage()).append("(").append("所传值:[").append(fieldError.getRejectedValue()).append("]")
                                .append("不符合规则:[").append(fieldError.getCode()).append("])").toString()));
        return resultWrapper;


    }

    private void log(Exception ex, HttpServletRequest request) {
        logger.error("************************异常开始*******************************");
        logger.error("请求地址：" + request.getRequestURL());
        Enumeration enumeration = request.getParameterNames();
        logger.error("请求参数");
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement().toString();
            logger.error(name + "---" + request.getParameter(name));
        }
        StackTraceElement[] error = ex.getStackTrace();
        for (StackTraceElement stackTraceElement : error) {
            logger.error(stackTraceElement.toString());
        }
        logger.error("************************异常结束*******************************");
    }


    //bean验证异常

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultWrapper handleResException(ConstraintViolationException e) {
        logger.info("验证异常!---------------------------------");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.NOT_VALID);
        Map<String, Object> map = resultWrapper.getValidMessage();
        violations.parallelStream().forEach(violation->
                map.put(violation.getPropertyPath().toString(),
                        new StringBuilder().append("所传值(").append(violation.getInvalidValue()).append(")提示信息:").append(violation.getMessage()))
        );
        resultWrapper.setData(violations);
        return resultWrapper;
    }

}


