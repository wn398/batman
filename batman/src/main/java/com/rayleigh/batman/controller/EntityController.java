package com.rayleigh.batman.controller;

import com.alibaba.fastjson.JSON;
import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.batman.uiModel.DataBaseConnectionModel;
import com.rayleigh.batman.uiModel.DataBaseConnectionWithEntityModel;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.DataBaseType;
import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.enums.PrimaryKeyType;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.batman.util.BatmanBaseModelUtil;
import com.rayleigh.core.util.StringUtil;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.ApiOperation;
import io.undertow.util.FileUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/entitiesCtl")
public class EntityController extends BaseController{
    @Autowired
    private EntityService entityService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EntityManager entityManager;

    @PostMapping(value = "/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@Valid @RequestBody Entities entities){
        if(!StringUtil.isCapFirst(entities.getName())){
            return getFailureResultAndInfo(null,"实体名称首字母不能小写!");
        }
        //添加对字段名字和描述不能为空的验证
        for(Field field:entities.getFields()){
            if(StringUtil.isEmpty(field.getName())||StringUtil.isEmpty(field.getDescription())){
                return getFailureResultAndInfo(field,new StringBuilder("字段名或描述不能为空!").toString());
            }
            if(!StringUtil.isUnCapFirst(field.getName())){
                return getFailureResultAndInfo(field,new StringBuilder("字段名首字母不能大写:").append(field.getName()).toString());
            }
            if(StringUtil.isDigitFirst(field.getName())){
                return getFailureResultAndInfo(field,new StringBuilder("字段首字母不能为数字！").toString());
            }
        }

        entities.getFields().parallelStream().forEach(field -> {if(StringUtil.isEmpty(field.getValidMessage())){field.setValidMessage(null);}});
        List<String> validationMessages = entities.getFields().parallelStream().map(field -> field.getValidMessage()).collect(Collectors.toList());
        for(String validationMessage:validationMessages){
                if(null!=validationMessage) {
                    String[] arrays = validationMessage.split("||");
                    for (int i = 0; i < arrays.length; i++) {
                        if (!arrays[i].contains("@")) {
                            return getFailureResultAndInfo(null, new StringBuilder("验证字段:").append(validationMessage).append("不符合验证规则!").toString());
                        }
                    }
                }

        }

        Entities resultEntities = (Entities) BatmanBaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(entities);
        Entities entities1 = entityService.save(resultEntities);
        moduleService.setUpdateDate(entities.getModule().getId(),new Date());
        //BatmanBaseModelUtil.preventMutualRef(entities1,new ArrayList());

        return getSuccessResult("新增成功！");
    }

    @PostMapping(value = "/partUpdate")
    @ResponseBody
    public ResultWrapper partUpdate(@RequestBody Entities entities,HttpServletRequest request){

        if(null!=entities&& !StringUtil.isEmpty(entities.getId())) {

            if(!StringUtil.isCapFirst(entities.getName())){
                return getFailureResultAndInfo(null,"实体名称首字母不能小写!");
            }
            //添加对字段名字和描述不能为空的验证
            for(Field field:entities.getFields()){
                if(StringUtil.isEmpty(field.getName())||StringUtil.isEmpty(field.getDescription())){
                    return getFailureResultAndInfo(field,new StringBuilder("字段名或描述不能为空!-").append(JSON.toJSON(field)).toString());
                }
                if(!StringUtil.isUnCapFirst(field.getName())){
                    return getFailureResultAndInfo(field,new StringBuilder("字段名首字母不能大写:").append(field.getName()).toString());
                }
                if(StringUtil.isDigitFirst(field.getName())){
                    return getFailureResultAndInfo(field,new StringBuilder("字段首字母不能为数字！").toString());
                }
            }
            //检测字段名是否重复
            Map<String,Long> map = entities.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.groupingBy(it->it,Collectors.counting()));
            if(map.size() !=entities.getFields().size()){
                List<String> list2 = map.entrySet().parallelStream().filter(it->it.getValue()>1).map(it->it.getKey()).collect(Collectors.toList());
                return getFailureResultAndInfo(list2, new StringBuilder("属性名重复:").append(list2.parallelStream().collect(Collectors.joining(","))).toString());
            }
            entities.getFields().parallelStream().forEach(field -> {if(StringUtil.isEmpty(field.getValidMessage())){field.setValidMessage(null);}});
            List<String> validationMessages = entities.getFields().parallelStream().map(field -> field.getValidMessage()).filter(message->!StringUtil.isEmpty(message)).collect(Collectors.toList());
            for(String validationMessage:validationMessages){
                if(!StringUtil.isEmpty(validationMessage)) {
                    String[] arrays = validationMessage.split("[||]");
                    for (int i = 0; i < arrays.length; i++) {
                        if (!StringUtil.isEmpty(arrays[i])&&!arrays[i].contains("@")) {
                            return getFailureResultAndInfo(null,new StringBuilder("验证字段:").append(validationMessage).append("不符合验证规则!").toString());
                        }
                    }
                }
            }
            //如果主键类型变了，要查看相关联的实体类型是否一致
            Entities dbEntities = entityService.findOne(entities.getId());
            if(dbEntities.getPrimaryKeyType()!=entities.getPrimaryKeyType()){
                if(dbEntities.getMainEntityRelationShips().size()>0){
                    return getFailureResultAndInfo(null,"不能修改主键类型，存在关联外键!");
                }
            }
            //处理修改实体名称
            Entities dataBaseEntity = entityService.findOne(entities.getId());
            if(!entities.getName().equals(dataBaseEntity.getName())){
                String realPath = request.getServletContext().getRealPath("/");
                //生成的每个项目根据时间生成对应目录
                String basePath = new StringBuilder("/").append(dataBaseEntity.getProject().getId()).toString();
                String generatorBasePath = new StringBuilder(realPath).append("/").append(basePath).toString();
                File file = new File(generatorBasePath);
                if(file.exists()){
                    File deleteFile = new File(generatorBasePath+"-delete-"+System.currentTimeMillis());
                    boolean success = file.renameTo(deleteFile);
                    try {
                        FileUtils.deleteRecursive(deleteFile.toPath());
                    }catch (Exception e){
                        e.printStackTrace();
                        logger.error(new StringBuilder("递归删除文件出错！,文件路径：").append(deleteFile.getAbsolutePath()).toString());
                    }
                    if(!success){
                        return getFailureResultAndInfo(null,"重命名实体类名称失败!");
                    }
                }

            }

            Entities entities1 = entityService.partUpdate(entities);
            moduleService.setUpdateDate(entities.getModule().getId(),new Date());
            return getSuccessResult("更新成功!");
        }else{
            return getFailureResultAndInfo(entities,"id不能空!");
        }
    }


    @RequestMapping("/goUpdate")
    public String goUpdate(HttpServletRequest request, Model model){
        String id = request.getParameter("id");
        if(!StringUtil.isEmpty(id)) {
            Entities entities = entityService.findOne(id);
            model.addAttribute("entities", entities);
            return "/page/entities-update";
        }else{
            return "/error";
        }
    }


    @DeleteMapping(value = "/doDelete")
    @ResponseBody
    public ResultWrapper delete(@RequestBody Entities entities){
        if(null!=entities&&!StringUtil.isEmpty(entities.getId())) {
            try {
                moduleService.setUpdateDate(entityService.findOne(entities.getId()).getModule().getId(),new Date());
                entityService.deleteOne(entities);
                return getSuccessResult(ResultStatus.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                return getFailureResult(ResultStatus.FAILURE);
            }
        }else{
            return getFailureResultAndInfo(entities,"请传入id");
        }
    }

    /**
     * 去一个实体类的方法列表页面
     */
    @RequestMapping("/showEntityMethod/{id}")
    public String showEntityMethod(Model model,@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        model.addAttribute("entity",entities);
        return "/page/method-list";
    }

    @PostMapping("/getEntityMethodData/{id}")
    @ResponseBody
    public ResultWrapper showEntityMethodData(@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        List<SearchMethod> methods = entities.getMethods();
        //设置默认值
        methods.parallelStream().forEach(method -> {
        if(null ==method.getIsReturnObject()){
            method.setIsReturnObject(false);
        if(null ==method.getIsInterface()){
            method.setIsInterface(true);
        }
        }});
        methods.parallelStream().forEach(method->{method.setConditionList(null);method.setSearchResults(null);
            method.setEntities(null);
        });

        return getSuccessResult(methods);
    }

    /**
     * 方法增加页面
     */
    @RequestMapping("/goAddMethod/{id}")
    public String goAddMethod(Model model,@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        model.addAttribute("entity",entities);
        return "/page/method-add";
    }
    //防止循环检测
    private Entities preventCirculation(Entities entities){
        if(null!=entities&&null!=entities.getModule()){
            entities.getModule().setEntities(null);
        }
        if(null!=entities&&entities.getFields().size()>0){
            entities.getFields().parallelStream().forEach(field -> field.setEntities(null));
        }
        return entities;
    }

    /**
     * 方法增加页面
     */
    @RequestMapping("/showConfig/{id}")
    public String showConfig(Model model,@PathVariable("id") String projectId){
        Project project = projectService.findOne(projectId);
        List<ProjectDataSource> dataSources = project.getProjectDataSources();
        model.addAttribute("dataSources",dataSources);
        return "/page/entities-config";
    }

    /**
     * 方法增加页面
     */
    @RequestMapping("/showConfigList/{id}")
    public String goConfigList(Model model,@PathVariable("id") String projectId){
        Project project = projectService.findOne(projectId);
        List<ProjectDataSource> dataSources = project.getProjectDataSources();
        List<Module> modules = project.getModules();
        model.addAttribute("dataSources",dataSources);
        model.addAttribute("modules",modules);
        return "/page/entities-config-list";
    }

    @ApiOperation("测试数据库连接功能!")
    @PostMapping("testDatabaseConnection")
    @ResponseBody
    public ResultWrapper testDataBaseConnection(@RequestBody DataBaseConnectionModel dataBaseConnectionModel){
        try (Connection connection = getConn(dataBaseConnectionModel)) {
            String sql = "select 1";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                getSuccessResult("连接成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getFailureResultAndInfo(e.getMessage(), "连接失败!");

        }
        return getSuccessResult("连接成功!");
    }

    @ApiOperation("获取数据源表名!")
    @PostMapping("getTableNames")
    @ResponseBody
    public ResultWrapper getTableNames(@RequestBody DataBaseConnectionModel dataBaseConnectionModel){
        try (Connection connection = getConn(dataBaseConnectionModel)) {
            String sql = null;
            PreparedStatement ps = null;
            if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.MySQL)){
                //sql = "select table_name as tablename from information_schema.tables where table_schema='"+dataBaseConnectionModel.getDataBaseName()+"' and table_type='base table' order by tablename";
                sql = "select table_name as tablename from information_schema.tables where table_schema=? and table_type='base table' order by tablename";
                ps = connection.prepareStatement(sql);
                ps.setString(1,dataBaseConnectionModel.getDataBaseName());
            }else if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.PostgreSql)){
                sql = "SELECT   tablename   FROM   pg_tables WHERE   tablename   NOT   LIKE   'pg%' AND tablename NOT LIKE 'sql_%' ORDER   BY   tablename";
                ps = connection.prepareStatement(sql);
            }else if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.Oracle)){
                //todo 未经测试
                sql = "select table_name from all_tables where owner = ?";
                ps = connection.prepareStatement(sql);
                ps.setString(1,dataBaseConnectionModel.getDataBaseName());
            }
            if(null==ps){
                return getFailureResultAndInfo(null,"数据库类型不在pg,mysql,oracle之一!");
            }
            ResultSet rs = ps.executeQuery();
            List<String> tableNames = new ArrayList<>();
            while (rs.next()) {
                tableNames.add(rs.getString(1));
            }
            return getSuccessResult(tableNames.parallelStream().collect(Collectors.joining(",")));
        } catch (Exception e) {
            e.printStackTrace();
            return getFailureResultAndInfo(e.getMessage(), "连接失败!");

        }
    }

    @ApiOperation("获取数据源表名及注释!")
    @PostMapping("getTableNamesAndComments")
    @ResponseBody
    public ResultWrapper getTableNamesComment(@RequestBody DataBaseConnectionModel dataBaseConnectionModel){
        try (Connection connection = getConn(dataBaseConnectionModel)) {
            String sql = null;
            PreparedStatement ps = null;
            if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.MySQL)){
               // sql = "select table_name as tablename,TABLE_COMMENT as comment from information_schema.tables where table_schema='"+dataBaseConnectionModel.getDataBaseName()+"' and table_type='base table'";
                sql = "select table_name as tablename,TABLE_COMMENT as comment from information_schema.tables where table_schema=? and table_type='base table'";
                ps = connection.prepareStatement(sql);
                ps.setString(1,dataBaseConnectionModel.getDataBaseName());
            }else if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.PostgreSql)){
                sql = "select relname as tablename,cast(obj_description(relfilenode,'pg_class') as varchar) as comment from pg_class c where  relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname";
                ps = connection.prepareStatement(sql);
            }else if(dataBaseConnectionModel.getDataBaseType().equals(DataBaseType.Oracle)){
                //todo
            }

            if(null==ps){
                return getFailureResultAndInfo(null,"数据库类型不在mysql,pg,oracle");
            }
            ResultSet rs = ps.executeQuery();
            List<Entities> entities = new ArrayList<>();
            while (rs.next()) {
                Entities entity = new Entities();
                entity.setName(StringUtil.capFirst(StringUtil.underlineToHump(rs.getString(1))));
                entity.setTableName(rs.getString(1));
                if(StringUtil.isEmpty(rs.getString(2))){
                    entity.setDescription("");
                }else {
                    entity.setDescription(rs.getString(2));
                }
                entities.add(entity);
            }
            return getSuccessResult(entities);
        } catch (Exception e) {
            e.printStackTrace();
            return getFailureResultAndInfo(e.getMessage(), "连接失败!");

        }
    }


    @ApiOperation("增加选定的表实体")
    @PostMapping("addMoreEntities")
    @ResponseBody
    public ResultWrapper addMoreEntities(@RequestBody DataBaseConnectionWithEntityModel dataBaseConnectionWithEntityModel){
        List<Entities> entitiesList = dataBaseConnectionWithEntityModel.getEntity();
        if(entitiesList.size()>0){
            moduleService.setUpdateDate(entitiesList.get(0).getModule().getId(),new Date());
        }
        entitiesList.stream().forEach(it-> {
                    dataBaseConnectionWithEntityModel.setTableName(it.getTableName());
                    List<Field> fieldList = (List<Field>)getTableColums(dataBaseConnectionWithEntityModel).getData();
                    it.setProject(moduleService.findOne(it.getModule().getId()).getProject());
                    it.setModule(moduleService.findOne(it.getModule().getId()));
                    String entityDescription = it.getDescription();
                    entityDescription = entityDescription.replaceAll("\r\n", " ").replaceAll("\r"," ").replaceAll("\n"," ").replaceAll("\t"," ").replaceAll("\"","'").replaceAll("/","-");
                    it.setDescription(entityDescription);
                    boolean isHaveId = false;
                    for(Field field:fieldList){
                        if(field.getName().equalsIgnoreCase("id")){
                            isHaveId = true;
                            if(field.getDataType().name().equals(DataType.String.name())){
                                it.setPrimaryKeyType(PrimaryKeyType.String);
                                break;
                            }else if(field.getDataType().name().equals(DataType.Long.name())){
                                it.setPrimaryKeyType(PrimaryKeyType.Long);
                                break;
                            }
                        }
                    }
                    //如果不包含id，则需要创建一个id字段
                    if(false == isHaveId){
                        Field field = new Field();
                        field.setDataType(DataType.String);
                        field.setDescription("生成程序创建的主键id");
                        field.setName("id");
                        fieldList.add(field);
                    }
                    //如果主键类型还为空，说明原实体中不存在id字段
                    if(null==it.getPrimaryKeyType()){
                        it.setPrimaryKeyType(PrimaryKeyType.String);
                    }
                    it.setFields(fieldList);
                    entityService.save(it);
                }
                );

        return getSuccessResult("新增成功!");
    }


    @ApiOperation("获取表字段!")
    @PostMapping("getTableColums")
    @ResponseBody
    public ResultWrapper getTableColums(@RequestBody DataBaseConnectionModel dataBaseConnectionModel){
        if(StringUtil.isEmpty(dataBaseConnectionModel.getTableName())){
            return getFailureResultAndInfo(null,"数据库表名不能为空!");
        }
        try (Connection connection = getConn(dataBaseConnectionModel)) {
            DataBaseType dataBaseType = dataBaseConnectionModel.getDataBaseType();
            String sql=null;
            PreparedStatement ps = null;
            if(dataBaseType.equals(DataBaseType.MySQL)){
                sql = "select COLUMN_NAME as name,COLUMN_COMMENT as description,data_TYPE as dataType from information_schema.columns where table_name= ? and table_schema=? ";
                ps = connection.prepareStatement(sql);
                ps.setString(1,dataBaseConnectionModel.getTableName());
                ps.setString(2,dataBaseConnectionModel.getDataBaseName());

            }else if(dataBaseType.equals(DataBaseType.PostgreSql)){

                sql = "SELECT a.attname as name, col_description(a.attrelid,a.attnum) as description, format_type(a.atttypid,a.atttypmod) as dataType  FROM pg_class as c,pg_attribute as a where c.relname = ? and a.attrelid = c.oid and a.attnum>0 and a.attname not like '%pg.%'";
                ps = connection.prepareStatement(sql);
                ps.setString(1,dataBaseConnectionModel.getTableName());

            }else if(dataBaseType.equals(DataBaseType.Oracle)){

            }

            if(null==ps){
                return getFailureResultAndInfo(null,"数据库类型不在mysql,pg,oracle之中!");
            }

            ResultSet rs = ps.executeQuery();
            String[] stringArray = {"varchar","char","text","json","character","mediumtext"};
            List<String> stringList = Collections.arrayToList(stringArray);

            String[] integerArray = {"tinyint","smallint","mediumint","int","bit"};
            List<String> integerList = Collections.arrayToList(integerArray);

            String[] doubArray = {"float","double"};
            List<String> doubleList = Collections.arrayToList(doubArray);

            String[] booleanArray = {"bit","boolean"};
            List<String> booleanList = Collections.arrayToList(booleanArray);

            String[] dateArray={"date","time","datetime","timestamp","year"};
            List<String> dateList = Collections.arrayToList(dateArray);

            String[] longArray={"integer","id","bigint"};
            List<String> longList = Collections.arrayToList(longArray);

            String[] bigDecimalArray = {"decimal","numeric","numeric(19,2)"};
            List<String> bigDecimalList = Collections.arrayToList(bigDecimalArray);

            List<Field> fieldsList = new ArrayList<>();
            while (rs.next()) {
                Field field = new Field();
                //下划线转驼峰
                String name = StringUtil.underlineToHump(rs.getString(1));
                String description = rs.getString(2);
                if(null!=description) {
                    description = description.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "'");
                }
                String dataType = rs.getString(3).toLowerCase();
                //获取pg前面的字符串
                dataType = dataType.split(" ")[0];
                field.setName(name);
                field.setDescription(description);
                if(stringList.contains(dataType)) {
                    field.setDataType(DataType.String);
                }else if(integerList.contains(dataType) || dataType.startsWith("bit")){
                    field.setDataType(DataType.Integer);
                }else if(dateList.contains(dataType)){
                    field.setDataType(DataType.Date);
                }else if(doubleList.contains(dataType)){
                    field.setDataType(DataType.Double);
                }else if(booleanList.contains(dataType)){
                    field.setDataType(DataType.Boolean);
                }else if(bigDecimalList.contains(dataType)||dataType.startsWith("numeric")){
                    field.setDataType(DataType.BigDecimal);
                }else if(longList.contains(dataType)){
                    field.setDataType(DataType.Long);
                }
                //如果判断不出来，默认给string类型
                if(null == field.getDataType()){
                    field.setDataType(DataType.String);
                }
                fieldsList.add(field);
            }
            return getSuccessResult(fieldsList);
        } catch (Exception e) {
            e.printStackTrace();
            return getFailureResultAndInfo(e.getMessage(), "连接失败!");

        }
    }

    /**
     * @Description 获取数据库连接
     * @param dataBaseConnectionModel
     * @return
     * @throws Exception
     */
    public Connection getConn(DataBaseConnectionModel dataBaseConnectionModel) throws Exception{
        DataBaseType dataBaseType = dataBaseConnectionModel.getDataBaseType();
        String driverClassName=null;
        String url=null;
        if(dataBaseType.equals(DataBaseType.PostgreSql)){
            driverClassName = "org.postgresql.Driver";
            url = new StringBuilder("jdbc:postgresql://").append(dataBaseConnectionModel.getHostName()).append(":").append(dataBaseConnectionModel.getPort()).append("/").append(dataBaseConnectionModel.getDataBaseName()).toString();
        }else if(dataBaseType.equals(DataBaseType.MySQL)){
            driverClassName = "com.mysql.jdbc.Driver";
            url = new StringBuilder("jdbc:mysql://").append(dataBaseConnectionModel.getHostName()).append(":").append(dataBaseConnectionModel.getPort()).append("/").append(dataBaseConnectionModel.getDataBaseName()).append("?serverTimezone=GMT%2B8").toString();
        }else if(dataBaseType.equals(DataBaseType.Oracle)){
            driverClassName = "oracle.jdbc.driver.OracleDriver";
            url = new StringBuilder("jdbc:oracle://").append(dataBaseConnectionModel.getHostName()).append(":").append(dataBaseConnectionModel.getPort()).append("/").append(dataBaseConnectionModel.getDataBaseName()).toString();
        }
        Class.forName(driverClassName);
        Connection c = DriverManager.getConnection(url, dataBaseConnectionModel.getUsername(), dataBaseConnectionModel.getPassword());
        return c;
    }
}
