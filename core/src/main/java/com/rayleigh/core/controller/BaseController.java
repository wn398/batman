package com.rayleigh.core.controller;

import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wangn20 on 2017/6/29.
 */
public abstract class BaseController<T extends BaseService,R extends BaseModel> {
    protected Logger logger= LoggerFactory.getLogger(getClass());


    protected ResultWrapper getSuccessResult(Object result){
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.SUCCESS);
        resultWrapper.setData(result);
        return resultWrapper;
    }

    protected ResultWrapper getFailureResult(Object result){
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.FAILURE);
        resultWrapper.setData(result);
        return resultWrapper;
    }

    protected ResultWrapper getFailureResultAndInfo(Object result,String info){
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.FAILURE);
        resultWrapper.setData(result);
        resultWrapper.setInfo(info);
        return resultWrapper;
    }

}
