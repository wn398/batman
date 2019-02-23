package com.rayleigh.batman.uiModel;

import com.rayleigh.core.enums.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("字段信息，名字，类型，描述，用于sqlMethod方法参数与结果包装类中")
public class FieldInfo {
    @ApiModelProperty("字段名字")
    private String name;
    @ApiModelProperty("字段说明")
    private String description;
    @ApiModelProperty("字段类型")
    private String dataType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
