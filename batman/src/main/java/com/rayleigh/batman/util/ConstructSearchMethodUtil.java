package com.rayleigh.batman.util;

import com.alibaba.fastjson.JSON;
import com.rayleigh.batman.model.*;
import com.rayleigh.core.enums.EntityRelationType;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.enums.Operation;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.model.SearchMethodConditionModel;
import com.rayleigh.core.model.SearchMethodResultModel;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ConstructSearchMethodUtil {
    private static Logger logger = LoggerFactory.getLogger(ConstructSearchMethodUtil.class);
    //根据condition的fieldName提取出按实体id分类的map对象
    public static Map<String,List<SearchCondition>> extractCondition(List<SearchCondition> conditions){
        return conditions.parallelStream().collect(Collectors.groupingBy(condition->condition.getFieldName().split("_")[0]));
    }
    //根据result的fieldName提取出按实体id分类的map对象
    public static Map<String,List<SearchResult>> extractResult(List<SearchResult> results){
        return results.parallelStream().collect(Collectors.groupingBy(result->result.getFieldName().split("_")[0]));
    }
    //按优先级提取出condition类型map，在做条件处理时，按优先级处理
    public static Map<Integer,List<SearchCondition>> groupCondition(List<SearchCondition> conditions){
        Map<Integer,List<SearchCondition>> map = conditions.parallelStream().collect(Collectors.groupingBy(condition->condition.getPriority()));
        return map;
    }

    //按优先级提取出condition类型map，在做条件处理时，按优先级处理,freemarker不支持数字作为key,转换成string
    public static Map<String,List<SearchCondition>> groupConditionKeyStr(List<SearchCondition> conditions){
        Map<Integer,List<SearchCondition>> map = conditions.parallelStream().collect(Collectors.groupingBy(condition->condition.getPriority()));
        Map<String, List<SearchCondition>> resultMap = new HashMap<>();
        for(Map.Entry<Integer,List<SearchCondition>> entry:map.entrySet()){
            resultMap.put(entry.getKey()+"",entry.getValue());
        }
        return resultMap;
    }

    //从一组条件里提取逻辑操作符
    public static LogicOperation getLogicOperationFromCondition(List<SearchCondition> conditions){
        Map<LogicOperation,List<SearchCondition>> map = conditions.parallelStream().collect(Collectors.groupingBy(condition->condition.getLogicOperation()));
        if(map.size()!=1){
            throw new RuntimeException("逻辑运算符在同一个优先级里相互不相同！！fieldName为"+conditions.parallelStream().map(condition->condition.getFieldName()).collect(Collectors.joining(",")));
        }else{
            return (LogicOperation) map.keySet().toArray()[0];
        }

    }

    //从一组结果里过滤并排序出排序结果，排序名越高，优先级越大，为0不参与排序
    public static List<SearchResult> getOrderResult(List<SearchResult> results){
        List<SearchResult> list = results.parallelStream().filter(searchResult -> searchResult.getOrderByNum()>0).sorted(new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return o2.getOrderByNum()-o1.getOrderByNum();
            }
        }).collect(Collectors.toList());
        if(null !=list &&list.size()>3){
            return list.subList(0,3);
        }
        return list;
    }

    //给定一个method，构造它的基础查询JPQL, select new Map ("a","b") from A
    public static String constructBasicJPQL(SearchMethod method, Entities mainEntity){
        String selectFrom = getSelectFrom(method,mainEntity);
        String where = getBasicWhereStr(method,mainEntity);
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where);

        return resultSb.toString();
    }
    //构建查询主对象的方法,select a from A as a
    public static String constructObjectJPQL(SearchMethod method,Entities mainEntity){
        String selectFrom = getObjectSelectFrom(method,mainEntity);
        String where = getObjectBasicWhereStr(method,mainEntity);
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where);

        return resultSb.toString();
    }
    //给定一个method,构建查询它数量的基础JPQL
    public static String constructBasicCountJPQL(SearchMethod method,Entities mainEntity){
        String selectFrom = getCountFrom(method,mainEntity);
        String where = getBasicWhereStr(method,mainEntity);
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where);

        return resultSb.toString();

    }
    //给定一个method,构建对象类型countJPQL
    public static String constructObjectBasicCountJPQL(SearchMethod method,Entities mainEntity){
        String selectFrom = getObjectCountFrom(method,mainEntity);
        String where = getObjectBasicWhereStr(method,mainEntity);
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where);

        return resultSb.toString();

    }
    //获取count类型的from字符串
    private static String getCountFrom(SearchMethod method,Entities mainEntity){
        Map<String,List<SearchResult>> groupByEntityIdMap = extractResult(method.getSearchResults());
        //小写的主对象名字
        String unCapMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
        StringBuilder select = new StringBuilder("select count(").append(unCapMainEntityName).append(") ");

        StringBuilder from = new StringBuilder(mainEntity.getName()).append(" as ").append(unCapMainEntityName);
        for(Map.Entry<String,List<SearchResult>> entry:groupByEntityIdMap.entrySet()){
            //对象名字
            String unCapOtherEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(entry.getKey()));
            //获取对象关系类型
            EntityRelationType entityRelationType = SearchDBUtil.getEntityRelationType(mainEntity.getId(),entry.getKey());
            if(entityRelationType == EntityRelationType.TableType) {
                //如果不是主对象，对它做关联
                if (!mainEntity.getId().equals(entry.getKey())) {
                    //获取主对象与它的关系
                    RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(), entry.getKey());
                    //如果是一对多，多对多
                    if (relationType == RelationType.OneToMany || relationType == RelationType.ManyToMany) {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                    } else {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                    }
                }
            }else{//处理字段关联
                if(!mainEntity.getId().equals(entry.getKey())) {
                    from.append(",").append(SearchDBUtil.getEntityName(entry.getKey())).append(" as ").append(unCapOtherEntityName);
                }
            }

        }
        return new StringBuilder(select).append(" from ").append(from.toString()).toString();
    }

    //获取对象 count类型的from字符串
    private static String getObjectCountFrom(SearchMethod method,Entities mainEntity){
        Map<String,List<SearchCondition>> groupByEntityIdMap = extractCondition(method.getConditionList());
        //小写的主对象名字
        String unCapMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
        StringBuilder select = new StringBuilder("select count(").append(unCapMainEntityName).append(") ");

        StringBuilder from = new StringBuilder(mainEntity.getName()).append(" as ").append(unCapMainEntityName);
        for(Map.Entry<String,List<SearchCondition>> entry:groupByEntityIdMap.entrySet()){
            //对象名字
            String unCapOtherEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(entry.getKey()));
            //获取对象关系类型
            EntityRelationType entityRelationType = SearchDBUtil.getEntityRelationType(mainEntity.getId(),entry.getKey());
            if(entityRelationType == EntityRelationType.TableType) {
                //如果不是主对象，对它做关联
                if (!mainEntity.getId().equals(entry.getKey())) {
                    //获取主对象与它的关系
                    RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(), entry.getKey());
                    //如果是一对多，多对多
                    if (relationType == RelationType.OneToMany || relationType == RelationType.ManyToMany) {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                    } else {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                    }
                }
            }else{//处理字段关联
                if(!mainEntity.getId().equals(entry.getKey())) {
                    from.append(",").append(SearchDBUtil.getEntityName(entry.getKey())).append(" as ").append(unCapOtherEntityName);
                }
            }

        }
        return new StringBuilder(select).append(" from ").append(from.toString()).toString();
    }

    //构建查询主对象类型 selectFrom查询字符串
    private static String getObjectSelectFrom(SearchMethod method,Entities mainEntity){
        Map<String,List<SearchCondition>> groupByEntityIdMap = extractCondition(method.getConditionList());
        //小写的主对象名字
        String unCapMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
        StringBuilder select = new StringBuilder("select ").append(unCapMainEntityName).append(" ");

        StringBuilder from = new StringBuilder(mainEntity.getName()).append(" as ").append(unCapMainEntityName);
        for(Map.Entry<String,List<SearchCondition>> entry:groupByEntityIdMap.entrySet()){
            //对象名字
            String unCapOtherEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(entry.getKey()));
            EntityRelationType entityRelationType = SearchDBUtil.getEntityRelationType(mainEntity.getId(),entry.getKey());
            if(entityRelationType == EntityRelationType.TableType) {
                //如果不是主对象，对它做关联
                if (!mainEntity.getId().equals(entry.getKey())) {
                    //获取主对象与它的关系
                    RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(), entry.getKey());
                    //如果是一对多，多对多
                    if (relationType == RelationType.OneToMany || relationType == RelationType.ManyToMany) {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                    } else {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                    }
                }
            }else{//处理字段关联
                if(!mainEntity.getId().equals(entry.getKey())) {
                    from.append(",").append(SearchDBUtil.getEntityName(entry.getKey())).append(" as ").append(unCapOtherEntityName);
                }
            }

        }
        return new StringBuilder(select.toString()).append(" from ").append(from.toString()).toString();
    }

    //构建selectFrom查询字符串
    private static String getSelectFrom(SearchMethod method,Entities mainEntity){
        Map<String,List<SearchResult>> groupByEntityIdMap = extractResult(method.getSearchResults());
        //小写的主对象名字
        String unCapMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
        StringBuilder select = new StringBuilder("select new Map(");

        StringBuilder from = new StringBuilder(mainEntity.getName()).append(" as ").append(unCapMainEntityName);
        for(Map.Entry<String,List<SearchResult>> entry:groupByEntityIdMap.entrySet()){
            //对象名字
            String unCapOtherEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(entry.getKey()));
            EntityRelationType entityRelationType = SearchDBUtil.getEntityRelationType(mainEntity.getId(),entry.getKey());
            if(entityRelationType == EntityRelationType.TableType) {
                //如果不是主对象，对它做关联
                if (!mainEntity.getId().equals(entry.getKey())) {
                    //获取主对象与它的关系
                    RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(), entry.getKey());
                    //如果是一对多，多对多
                    if (relationType == RelationType.OneToMany || relationType == RelationType.ManyToMany) {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                    } else {
                        from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                    }
                }
            }else{//处理字段关联
                if(!mainEntity.getId().equals(entry.getKey())) {
                    from.append(",").append(SearchDBUtil.getEntityName(entry.getKey())).append(" as ").append(unCapOtherEntityName);
                }
            }

            for (SearchResult searchResult : entry.getValue()) {
                String fieldName;
                if (null == searchResult.getField()) {
                    fieldName = searchResult.getFieldName().split("_")[1];
                } else {
                    fieldName = searchResult.getField().getName();
                }
                select.append(unCapOtherEntityName).append(".").append(fieldName).append(" as ").append(unCapOtherEntityName).append(StringUtil.capFirst(fieldName)).append(",");
            }

        }

        String selectStr = select.toString().substring(0,select.toString().length()-1);

        return new StringBuilder(selectStr).append(")").append(" from ").append(from.toString()).toString();
    }
    //获取orderyBy的json字串
    public static String getDynamicOrderByJSON(SearchMethod method,Entities mainEntity){
        List<SearchResult> filteredResult = method.getSearchResults().parallelStream().filter(searchResult -> null !=searchResult.getOrderByType()&&searchResult.getOrderByNum()>0).collect(Collectors.toList());
        List<SearchMethodResultModel> searchMethodResultModelList = new ArrayList<>();
        for(SearchResult searchResult:filteredResult){
            searchMethodResultModelList.add(SearchResult.toResultModel(searchResult));
        }
        return JSON.toJSONString(searchMethodResultModelList).replaceAll("[\"]","'");
    }


    //获取所有where条件的json字符串
    public static String getDynamicWhereJSON(SearchMethod method, Entities mainEntity){
        Map<String,SearchMethodConditionModel> conditionModelMap = new HashMap<>();
        for(SearchCondition condition:method.getConditionList()){
            SearchMethodConditionModel conditionModel = SearchCondition.toConditionModel(condition);
            conditionModelMap.put(conditionModel.getConditionName(),conditionModel);
        }
        if(conditionModelMap.size()>0) {
            return JSON.toJSONString(conditionModelMap).replaceAll("[\"]","'");
        }else{
            return "";
        }
    }

    //获取where 字符串
    private static String getBasicWhereStr(SearchMethod method, Entities mainEntity) {
        Map<String,List<SearchResult>> groupByEntityIdMap = extractResult(method.getSearchResults());
        //按主对象与副对象关系类型分组
        Map<EntityRelationType, List<String>> entityRelationMap = new HashMap<>();
        for(String otherId:groupByEntityIdMap.keySet()){
            EntityRelationType entityRelationType=SearchDBUtil.getEntityRelationType(mainEntity.getId(),otherId);
            if(entityRelationMap.containsKey(entityRelationType)){
                entityRelationMap.get(entityRelationType).add(otherId);
            }else{
                List<String> list = new ArrayList<>();
                list.add(otherId);
                entityRelationMap.put(entityRelationType,list);
            }

        }
        StringBuilder where = new StringBuilder(" where ");
        if(null!=entityRelationMap.get(EntityRelationType.FieldType)){
            for(String otherId:entityRelationMap.get(EntityRelationType.FieldType)){
                String unCapFirstMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
                String unCapFirstEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(otherId));
                FieldRelationShip fieldRelationShip = SearchDBUtil.getFieldRelationShipByMainOtherEntityId(mainEntity.getId(),otherId);
                if(where.toString().length()>7){
                    where.append(" and ");
                }
                where.append(" ").append(unCapFirstMainEntityName).append(".").append(fieldRelationShip.getMainField().getName()).append(" = ").append(unCapFirstEntityName).append(".").append(fieldRelationShip.getOtherField().getName()).append(" ");
            }
            if(entityRelationMap.get(EntityRelationType.FieldType).size()>0 && method.getConditionList().size()>0){
                where.append(" and ");
            }
        }
        return where.toString();
    }

    //获取object主对象类型的basicWhereStr
    private static String getObjectBasicWhereStr(SearchMethod method, Entities mainEntity) {
        Map<String,List<SearchCondition>> groupByEntityIdMap = extractCondition(method.getConditionList());
        //按主对象与副对象关系类型分组
        Map<EntityRelationType, List<String>> entityRelationMap = new HashMap<>();
        for(String otherId:groupByEntityIdMap.keySet()){
            EntityRelationType entityRelationType=SearchDBUtil.getEntityRelationType(mainEntity.getId(),otherId);
            if(entityRelationMap.containsKey(entityRelationType)){
                entityRelationMap.get(entityRelationType).add(otherId);
            }else{
                List<String> list = new ArrayList<>();
                list.add(otherId);
                entityRelationMap.put(entityRelationType,list);
            }

        }
        StringBuilder where = new StringBuilder(" where ");
        if(null!=entityRelationMap.get(EntityRelationType.FieldType)) {
            for (String otherId : entityRelationMap.get(EntityRelationType.FieldType)) {
                String unCapFirstMainEntityName = StringUtil.unCapFirst(mainEntity.getName());
                String unCapFirstEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(otherId));
                FieldRelationShip fieldRelationShip = SearchDBUtil.getFieldRelationShipByMainOtherEntityId(mainEntity.getId(), otherId);
                if (where.toString().length() > 7) {
                    where.append(" and ");
                }
                where.append(" ").append(unCapFirstMainEntityName).append(".").append(fieldRelationShip.getMainField().getName()).append(" = ").append(unCapFirstEntityName).append(".").append(fieldRelationShip.getOtherField().getName()).append(" ");
            }
            if (entityRelationMap.get(EntityRelationType.FieldType).size()>0 && method.getConditionList().size() > 0) {
                where.append(" and ");
            }
        }
        return where.toString();
    }

    //构建返回字段类型结果包装类的jpQl, 全条件传递
    public static String constructFullFieldsJPQL(SearchMethod searchMethod,Entities mainEntity){
        String selectFrom = getSelectFrom(searchMethod,mainEntity);
        String where = getWhere(searchMethod);
        String orderBy = getOrderBy(searchMethod,mainEntity);
        if(!selectFrom.contains("where")){
            where = " where "+where;
        }
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where).append(orderBy);

        return resultSb.toString();
    }

    //构建返回主对象类型结果包装类的jpql,全条件传递
    public static String constructFullObjectJPQL(SearchMethod searchMethod,Entities mainEntity){
        String selectFrom = getObjectSelectFrom(searchMethod,mainEntity);
        String where = getWhere(searchMethod);
        String orderBy = getOrderBy(searchMethod,mainEntity);
        if(!selectFrom.contains("where")){
            where = " where "+where;
        }
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where).append(orderBy);

        return resultSb.toString();
    }

    //构建查询数量，全条件传递
    public static String constructFullCountJPQL(SearchMethod searchMethod,Entities mainEntity){
        String selectFrom = getObjectCountFrom(searchMethod,mainEntity);
        String where = getWhere(searchMethod);
        if(!selectFrom.contains("where")){
            where = " where "+where;
        }
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where);

        return resultSb.toString();
    }

    //获取全条件查询的where 字符串
    private static String getWhere(SearchMethod method) {
        StringBuilder where = new StringBuilder();
        //根据查询条件的优先级排序
        Map<Integer,List<SearchCondition>> groupByPriorityMap = groupCondition(method.getConditionList());

        List<Map.Entry<Integer,List<SearchCondition>>> list = new ArrayList<>(groupByPriorityMap.entrySet());
        //如果存在多个级别就进行排序
        if (groupByPriorityMap.size() > 1){
            Collections.sort(list, new Comparator<Map.Entry<Integer, List<SearchCondition>>>() {
                @Override
                public int compare(Map.Entry<Integer, List<SearchCondition>> o1, Map.Entry<Integer, List<SearchCondition>> o2) {
                    return o2.getKey() - o1.getKey();
                }
            });
        }
        //处理最外层条件
        List<String> conditions = new ArrayList<>();
        for(Map.Entry<Integer,List<SearchCondition>> entry:list){
            //当前遍历级别下的逻辑操作符
            LogicOperation logicOperation =getLogicOperationFromCondition(entry.getValue());
            //里面的条件
            List<String> subCondition = new ArrayList<>();
            //对一个级别进行处理
            for(SearchCondition condition:entry.getValue()){
                //操作类型
                Operation operation = condition.getOperation();
                String unCapEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(condition.getFieldName().split("_")[0]));;
                String fieldName;
                if(null == condition.getField()){
                    fieldName = condition.getFieldName().split("_")[1];
                }else{
                    fieldName = condition.getField().getName();
                }
                switch (operation) {
                    case Equal:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append("=").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case GreaterThan:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(">").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case Between:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(" between ").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).append("Min").append(" and ").append(":").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).append("Max").toString());
                        break;
                    case GreaterThanOrEqualTo:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(">=").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case In:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(" in ").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).append("List").toString());
                        break;
                    case IsNull:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(" is ").append(" null ").toString());
                        break;
                    case IsNotNull:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(" is ").append(" not ").append(" null ").toString());
                        break;
                    case LessThan:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append("<").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case Like:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append(" like ").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case LessThanOrEqualTo:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append("<=").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                    case NotEqual:
                        subCondition.add(new StringBuilder(unCapEntityName).append(".").append(fieldName).append("!=").append(" :").append(unCapEntityName).append(StringUtil.capFirst(fieldName)).toString());
                        break;
                }
            }
            if(subCondition.size()>1){
                String str = subCondition.parallelStream().collect(Collectors.joining(" "+logicOperation+" "));
                conditions.add(new StringBuilder(" ( ").append(str).append(" ) ").toString());
            }else if(subCondition.size()==1){
                conditions.add(subCondition.get(0));
            }
        }

        if(conditions.size()>1){
            String str = conditions.parallelStream().collect(Collectors.joining(" and "));
            where.append(str);
        }else if(conditions.size()==1){
            String str = conditions.get(0).replaceAll(" [(] "," ").replaceAll(" [)] "," ");
            where.append(str);
        }
        return where.toString();
    }

    //获取orderBy查询字符串（排序 现在按结果的类型和优先级排序,优先级默认为0，表示没有设置优先级，级数越高，越优先）
    private static String getOrderBy(SearchMethod method,Entities mainEntity){
        StringBuilder orderBy = new StringBuilder(" order by ");
        List<String> orderByList = new ArrayList<>();
        //过滤出非0的排序result
        List<SearchResult> filteredResult = method.getSearchResults().parallelStream().filter(searchResult -> null !=searchResult.getOrderByType()&&searchResult.getOrderByNum()>0).collect(Collectors.toList());
        if(null!=filteredResult&&filteredResult.size()>0){
            filteredResult.parallelStream().collect(Collectors.groupingBy(searchResult->searchResult.getOrderByNum()));
            //倒序排列
            Collections.sort(filteredResult, new Comparator<SearchResult>() {
                @Override
                public int compare(SearchResult o1, SearchResult o2) {
                    return o2.getOrderByNum()-o1.getOrderByNum();
                }
            });

            for(int i=0;i<filteredResult.size();i++){
                String entityName = SearchDBUtil.getEntityName(filteredResult.get(i).getFieldName().split("_")[0]);
                String fieldName;
                String orderByType = filteredResult.get(i).getOrderByType().toString();
                if(null ==filteredResult.get(i).getField()){
                    fieldName = filteredResult.get(i).getFieldName().split("_")[1];
                }else{
                    fieldName = filteredResult.get(i).getField().getName();
                }
                orderByList.add(new StringBuilder(StringUtil.unCapFirst(entityName)).append(".").append(fieldName).append(" ").append(orderByType).toString());
            }
        }else{
            //默认按主对象的更新时间倒序,先去掉
            //orderByList.add(new StringBuilder(StringUtil.unCapFirst(mainEntity.getName())).append(".updateDate").append(" desc").toString());
        }


        if(orderByList.size()>0){
            //如果有超过两个，就留两个排序字段
            if(orderByList.size()>2){
                for(int i=2;i<orderByList.size();i++){
                    orderByList.remove(i);
                }
            }
            orderBy.append(orderByList.stream().collect(Collectors.joining(",")));
        }else{
            //如果为空，则返回空字符串
            return "";
        }

        return orderBy.toString();

    }

}