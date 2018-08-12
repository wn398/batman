package com.rayleigh.core.model;

import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.enums.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 条件模型
 */
//@ApiModel("查询方法条件model")
public class SearchMethodConditionModel {

    @ApiModelProperty("优先级")
    private Integer priority = 0;


    @ApiModelProperty("逻辑操作符")
    private LogicOperation logicOperation = LogicOperation.and;


    @ApiModelProperty("字段所在实体名")
    private String entityName;


    @ApiModelProperty("字段名称")
    private String fieldName;


    @ApiModelProperty("操作符")
    private Operation operation;


    @ApiModelProperty("包装类中条件的名字")
    private String conditionName;

    @ApiModelProperty("字段的数据类型")
    private DataType fieldDataType;

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LogicOperation getLogicOperation() {
        return logicOperation;
    }

    public void setLogicOperation(LogicOperation logicOperation) {
        this.logicOperation = logicOperation;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public DataType getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(DataType fieldDataType) {
        this.fieldDataType = fieldDataType;
    }
}
