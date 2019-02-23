package com.rayleigh.batman;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.rayleigh.batman.uiModel.ParseSqlSelectItemModel;
import com.rayleigh.core.util.StringUtil;
import java.util.*;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        //String sql = "SELECT 1 as test, (select id from test) as test_id, project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.hierachy_date AS hierachyDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = ? GROUP BY project.ID";
        String sql = "select a.city_name, a.id, a.estate_name, a.salesforce_id, a.city_id\n" +
                "  from mall2db.item_estate a\n" +
                " where a.city_name not in ('深圳', '东莞', '石家庄', '斯德哥尔摩')\n" +
                "   and a.id not in ('12871', '20356', '13039', '12933', '13587', '10002', '20123', '13352', '10047', '10030', '20122', '20696')\n" +
                "   and ifnull(a.tyht_code, '') = ''\n" +
                "   -- 2=北京; 169=上海; 171=无锡; 180=苏州; 200=镇江; 210=杭州; 214=宁波; 218=温州; 221=嘉兴; 243=合肥; 244=芜湖; 288=南昌; 358=郑州; 434=长沙; 613=西安\n" +
                "   and a.city_id not in ('2', '169', '171', '180', '200', '210', '214', '218', '221', '243', '244', '288', '358', '434', '613')\n" +
                "   and not exists (select 1 from zhikeqy.qy_id_relation q where q.table_name = 'product_main_product' and q.column_name = 'pro_code' and q.create_user = 'migrate8'\n" +
                "                      and ifnull(q.fxj_id, '') = a.id)\n" +
                " order by a.city_id;";

        String sql2 = "SELECT 1 as test, (select id from test) as test_id, project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.hierachy_date AS hierachyDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = :userId GROUP BY project.ID";

        parseAll(sql2,JdbcConstants.MYSQL);

        getAlias(sql2,JdbcConstants.MYSQL);

        getFiledNameTableName(sql2,JdbcConstants.MYSQL);


        getParseSqlSelectItemModel(sql2,JdbcConstants.MYSQL);

    }

    public static  void getSelectItems(){
        final String dbType = JdbcConstants.MYSQL; // 可以是ORACLE、POSTGRESQL、SQLSERVER、ODPS等
        String sql = "SELECT 1 as test, (select id from test) as test_id, project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.hierachy_date AS hierachyDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = :userId GROUP BY project.ID";


    }

    public static String sqlFormat(String sql,String dbType){
        String result = SQLUtils.format(sql, dbType);
        return result;
    }


    //原味解析所有
    public static String parseAll(String sql,String dbType){


        //使用mysql解析
        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql) ;

        //解析select查询
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseSelect() ;
        SQLSelect sqlSelect = sqlStatement.getSelect() ;

        //获取sql查询块
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock)sqlSelect.getQuery() ;

        StringBuffer out = new StringBuffer() ;
        //创建sql解析的标准化输出
        SQLASTOutputVisitor sqlastOutputVisitor = SQLUtils.createFormatOutputVisitor(out , null , JdbcUtils.MYSQL) ;

        MySqlExportParameterVisitor exportParameterVisitor = new MySqlExportParameterVisitor();
        sqlStatement.accept(exportParameterVisitor);
        List<Object> list = exportParameterVisitor.getParameters();
        System.out.println(list);

        //解析select项
        out.delete(0, out.length()) ;
        for (SQLSelectItem sqlSelectItem : sqlSelectQuery.getSelectList()) {
            if(out.length()>1){
                out.append(",") ;
            }
            sqlSelectItem.accept(sqlastOutputVisitor);
        }
        System.out.println("SELECT "+out) ;

        //解析from
        out.delete(0, out.length()) ;
        sqlSelectQuery.getFrom().accept(sqlastOutputVisitor) ;
        System.out.println("FROM "+out) ;

        //解析where
        out.delete(0, out.length()) ;
        sqlSelectQuery.getWhere().accept(sqlastOutputVisitor) ;
        System.out.println("WHERE "+out);

        return null;
    }

    /**
     * 解析出查询sql的查询部分封装模型
     * @param sql
     * @param dbType
     * @return
     */
    public static List<ParseSqlSelectItemModel> getParseSqlSelectItemModel(String sql,String dbType){
        List<ParseSqlSelectItemModel> list = new ArrayList<>();

        SQLStatementParser sqlStatementParser = new SQLStatementParser(sql);

        //解析select查询
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseSelect() ;
        SQLSelect sqlSelect = sqlStatement.getSelect() ;
        //获取sql查询块
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock)sqlSelect.getQuery() ;

        List<SQLSelectItem> sqlSelectItemList = sqlSelectQuery.getSelectList();

        for(SQLSelectItem item: sqlSelectItemList){
            ParseSqlSelectItemModel parseSqlSelectItemModel = new ParseSqlSelectItemModel();
            String expr = item.getExpr().toString();
            parseSqlSelectItemModel.setSqlExp(item.getExpr().toString());
            String alias = item.getAlias();
            parseSqlSelectItemModel.setAliases(alias);
            String fieldName;
            String tableName;
            String tableAlias;
            if(StringUtil.isEmpty(alias)){
                if(expr.contains(".")) {
                    fieldName = expr.substring(expr.lastIndexOf(".") + 1, expr.length());
                }else{
                    fieldName = expr;
                }
            }else{
                String tableField = Arrays.asList(expr.split("[' '*]")).stream().filter(it-> !StringUtil.isEmpty(it)).collect(Collectors.toList()).get(0);
                if(expr.contains(".")) {
                    System.out.println(expr);
                    fieldName = tableField.substring(expr.lastIndexOf(".") + 1, expr.length());
                }else{
                    fieldName = tableField;
                }
            }
            parseSqlSelectItemModel.setTableName(fieldName);




            //parseSqlSelectItemModel.setFieldName();





        }


        StringBuffer out = new StringBuffer() ;
        //创建sql解析的标准化输出
        SQLASTOutputVisitor sqlastOutputVisitor = SQLUtils.createFormatOutputVisitor(out , null , JdbcUtils.MYSQL) ;

        //解析select项
        out.delete(0, out.length()) ;
        for (SQLSelectItem sqlSelectItem : sqlSelectQuery.getSelectList()) {
            if(out.length()>1){
                out.append(",") ;
            }
            sqlSelectItem.accept(sqlastOutputVisitor);
        }

        System.out.println("SELECT "+out) ;

        return null;

    }

    /**
     * 获取sql中的字段名，表名的map
     * @param sql
     * @param dbType
     * @return
     */
    public static Map<String,String> getFiledNameTableName(String sql,String dbType){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        Map<String,String> fieldNameTableNameMap = new HashMap<>();

        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            SchemaStatVisitor visitor = new SchemaStatVisitor();
            stmt.accept(visitor);

            for(TableStat.Column column:visitor.getColumns()){
                fieldNameTableNameMap.put(column.getName(),column.getTable());
            }

        }

        return fieldNameTableNameMap;
    }

    public static Map<String,String> getAlias(String sql,String dbType){

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        Map<String,SQLTableSource> tableSourceMap = visitor.getAliasMap();
        Map<String,SQLSelectItem> itemMap = visitor.getItemAliasMap();

        System.out.println(tableSourceMap);
        System.out.println(itemMap);

        return Collections.singletonMap("","");
    }

    public static class ExportTableAliasVisitor extends MySqlASTVisitorAdapter {
        private Map<String, SQLTableSource> aliasMap = new HashMap<String, SQLTableSource>(); //获取表名，别名
        private Map<String, SQLSelectItem> itemAliasMap = new HashMap<String, SQLSelectItem>();//字段别名
        public SQLSelectStatement sqlSelectStatement;
        @Override
        public boolean visit(SQLExprTableSource x) {
            String alias = x.getAlias();
            aliasMap.put(alias, x);
            return true;
        }

        @Override
        public boolean visit(SQLSelectItem x){
            String alias = x.getAlias();

            itemAliasMap.put(alias, x);
            return true;
        }

        @Override
        public boolean visit(SQLSelectQueryBlock x){

            return true;
        }



        @Override
        public boolean visit(SQLSelectStatement x){
            this.sqlSelectStatement = x;
            return true;
        }

        @Override
        public boolean visit(SQLValuesTableSource x){

            return true;
        }


        public Map<String, SQLTableSource> getAliasMap() {
            return aliasMap;
        }

        public Map<String, SQLSelectItem> getItemAliasMap(){
            return itemAliasMap;
        }

        public SQLSelectStatement getSqlSelectStatement(){
            return sqlSelectStatement;
        }

    }

}