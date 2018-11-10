package com.rayleigh.batman.model;

import com.rayleigh.batman.util.SearchDBUtil;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.enums.Operation;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.model.SearchMethodConditionModel;
import com.rayleigh.core.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_search_condition")
public class SearchCondition extends BaseModel{



    @ApiModelProperty("优先级别")
    @Column
    private Integer priority = 0;

    @ApiModelProperty("逻辑操作符")
    @Enumerated(EnumType.STRING)
    @Column
    private LogicOperation logicOperation = LogicOperation.and;

    @ApiModelProperty("属性")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_id")
    private Field field;

    //因为id,createDate,updateDate都是通过继承得到的，在属性里拿不到，所以要用此字段来表示，当然此时属性id就不存在，结果用（实体id_createDate类似表示）
    @ApiModelProperty("属性名字,此属性只适用于id,createDate,updateDate,现在全部属性放在数据库了，保留字段")
    @Column
    @NotEmpty
    private String fieldName;

    @ApiModelProperty("操作符")
    @Enumerated(EnumType.STRING)
    @Column
    @NotNull
    private Operation operation;

    @ManyToOne
    @JoinColumn(name = "search_method_id")
    private SearchMethod searchMethod;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public SearchMethod getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(SearchMethod searchMethod) {
        this.searchMethod = searchMethod;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    //转化成conditionModel
    public static SearchMethodConditionModel toConditionModel(SearchCondition condition){
        SearchMethodConditionModel conditionModel = new SearchMethodConditionModel();
        conditionModel.setPriority(condition.getPriority());
        //获取字段名称
        conditionModel.setFieldName(condition.getFieldName().substring(condition.getFieldName().indexOf("_")+1));
        conditionModel.setEntityName(SearchDBUtil.getEntityName(condition.getFieldName().split("_")[0]));
        conditionModel.setLogicOperation(condition.getLogicOperation());
        conditionModel.setOperation(condition.getOperation());
        if(condition.getOperation()==Operation.Between){
            conditionModel.setConditionName(new StringBuilder(StringUtil.unCapFirst(conditionModel.getEntityName())).append(StringUtil.capFirst(conditionModel.getFieldName())).append("BetweenValue").toString());
        }else if(condition.getOperation() == Operation.In){
            conditionModel.setConditionName(new StringBuilder(StringUtil.unCapFirst(conditionModel.getEntityName())).append(StringUtil.capFirst(conditionModel.getFieldName())).append("InList").toString());
        }else{
            conditionModel.setConditionName(new StringBuilder(StringUtil.unCapFirst(conditionModel.getEntityName())).append(StringUtil.capFirst(conditionModel.getFieldName())).toString());
        }
        conditionModel.setFieldDataType(condition.getField().getDataType());
        return conditionModel;
    }
}
