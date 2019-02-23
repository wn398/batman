package com.rayleigh.batman.uiModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("sql结果解析字段")
public class ParseSqlSelectItemModel {

    @ApiModelProperty("原表达式")
    private String sqlExp;
    @ApiModelProperty("实际表名")
    private String tableName;
    @ApiModelProperty("表名别名")
    private String tableAliase;
    @ApiModelProperty("字段名")
    private String fieldName;
    @ApiModelProperty("别名")
    private String aliases;
    @ApiModelProperty("数据类型")
    private String dataType;
    @ApiModelProperty("备注")
    private String remark;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSqlExp() {
        return sqlExp;
    }

    public void setSqlExp(String sqlExp) {
        this.sqlExp = sqlExp;
    }

    public String getTableAliase() {
        return tableAliase;
    }

    public void setTableAliase(String tableAliase) {
        this.tableAliase = tableAliase;
    }
}
