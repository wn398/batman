package com.rayleigh.batman;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.rayleigh.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class Test2 {
    public static void main(String[] args) {
//        String test="test1 test2   test3     test4";
//
//        String test2 = test.replaceAll("\\s{1,}", " ");
//        System.out.println(test2);

        //Arrays.asList(test.split("' '{1,}")).stream().forEach(it-> System.out.println(it));

        String sql = "SELECT 1 as test, (select id from test) as test_id, project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.hierachy_date AS hierachyDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = ? GROUP BY project.ID";

        // 新建 MySQL Parser
        SQLStatementParser parser = new MySqlStatementParser(sql);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println(visitor.getColumns());
        System.out.println(visitor.getOrderByColumns());

    }

    /**
     * 去除sql中多余的空格，只保留一个空格
     * @param sql
     * @return
     */
    public static String trimSql(String sql){
        return sql.replaceAll("\\s{1,}", " ");
    }

    /**
     * 参数化sql输出
     */
    public static void test222(){
        String sql = "select rownum,organ_level4,organ_name4,user_no,user_name,pzl_num,xf_ccn from (select t.organ_level4,t.organ_name4,t.user_no,t.user_name,sum(t.pzl_num) pzl_num,sum(t.xf_ccn) xf_ccn from rt_smtj_tb t,sm_user_organ_tb a,sm_organ_tb b where 2>1 and a.organ_no=b.organ_no and b.organ_no=t.organ_level4 and b.organ_level=4 and a.user_no='#DEAL_USERNO#' group by t.organ_level4,t.organ_name4,t.user_no,t.user_name order by sum(t.pzl_num) desc,user_no)";
//String sql = "select id,name from lhm where id=1 and name='lihaiming' and 1=3";
//String sql = "select a,b from c where a='33' and b='ddd'";
//String sql = "select b,a from c where a='33' and b='ddd' and a in ('33','332','334')";

//指定数据库类型
        String dbtype = JdbcConstants.MYSQL;
        String fs = ParameterizedOutputVisitorUtils.parameterize(sql, dbtype);
        System.out.println("fs:\n"+fs);
    }

}
