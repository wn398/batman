package com.rayleigh.core.dynamicDataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
   
    /*
     * 代码中的determineCurrentLookupKey方法取得一个字符串，
     * 该字符串将与配置文件中的相应字符串进行匹配以定位数据源，配置文件中数据源的名字name
     */
    @Override
    protected Object determineCurrentLookupKey() {
       /*
        * DynamicDataSourceContextHolder代码中使用setDataSourceType
        * 设置当前的数据源，在路由类中使用getDataSourceType进行获取，
        *  交给AbstractRoutingDataSource进行注入使用。
        */
       return DynamicDataSourceContextHolder.getDataSourceType();
    }
}