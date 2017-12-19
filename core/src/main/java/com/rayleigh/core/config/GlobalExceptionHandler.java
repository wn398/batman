package com.rayleigh.core.config;

import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //拦截请求参数验证不通过的请求，封装后返回友好的格式
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultWrapper handleIOException(HttpServletRequest request, HttpServletResponse response, Model model, MethodArgumentNotValidException e) {
        e.printStackTrace();
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

    //bean验证异常
    //拦截bean验证不通过导致的事务异常
    @ExceptionHandler(value = TransactionSystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultWrapper handleResException(TransactionSystemException e, HttpServletResponse response, HttpServletRequest request) {
        e.printStackTrace();
        ResultWrapper resultWrapper = new ResultWrapper();
        //如果是验证导致的
        if (e.getRootCause() instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ((TransactionSystemException) e).getRootCause()).getConstraintViolations();
            resultWrapper.setStatus(ResultStatus.NOT_VALID);
            Map<String, Object> map = new HashMap<>();
            violations.parallelStream().forEach(violation ->
                    map.put(violation.getPropertyPath().toString(),
                            new StringBuilder().append("所传值(").append(violation.getInvalidValue()).append("):").append(violation.getMessage()))
            );
            resultWrapper.setValidMessage(map);
            return resultWrapper;
        } else {
            //设置状态异常
            resultWrapper.setStatus(ResultStatus.EXCEPTION);
            StringBuilder sb = new StringBuilder(e.getMessage());
            for(StackTraceElement element:e.getStackTrace()){
                sb.append(element.toString()).append(" <br> ");
            }
            resultWrapper.setExceptionMessage(sb.toString());
        }
        return resultWrapper;
    }

    //所有异常
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultWrapper runtimeExceptionHandler(Exception e) {
        e.printStackTrace();
        ResultWrapper resultWrapper = new ResultWrapper();
        //设置状态异常
        resultWrapper.setStatus(ResultStatus.EXCEPTION);
        StringBuilder sb = new StringBuilder(e.getMessage()).append(" <br> ");
        for(StackTraceElement element:e.getStackTrace()){
            sb.append(element.toString()).append(" <br> ");
        }
        resultWrapper.setExceptionMessage(sb.toString());
        return resultWrapper;
    }


}


