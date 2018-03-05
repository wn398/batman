package com.rayleigh.batman.util;

import com.rayleigh.batman.model.*;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.enums.Operation;
import com.rayleigh.core.enums.RelationType;
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

    //给定一个method，构造它的jpql
    public static String constructJPQL(SearchMethod method, Entities mainEntity){
        String selectFrom = getSelectFrom(method,mainEntity);
        String where = getWhere(method);
        String orderBy = getOrderBy(method,mainEntity);
        //组合结果
        StringBuilder resultSb = new StringBuilder(selectFrom).append(where).append(orderBy);

        return resultSb.toString();
    }

    public static String constructCountJPQL(SearchMethod method,Entities mainEntity){
        String selectFrom = getCountFrom(method,mainEntity);
        String where = getWhere(method);
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
            //如果不是主对象，对它做关联
            if(!mainEntity.getId().equals(entry.getKey())){
                //获取主对象与它的关系
                RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(),entry.getKey());
                //如果是一对多，多对多
                if(relationType ==RelationType.OneToMany||relationType == RelationType.ManyToMany){
                    from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                }else{
                    from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                }
            }

        }
        return new StringBuilder(select).append(" from ").append(from.toString()).toString();
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
            //如果不是主对象，对它做关联
            if(!mainEntity.getId().equals(entry.getKey())){
                //获取主对象与它的关系
                RelationType relationType = SearchDBUtil.getRelationTypeByMainOtherEntityId(mainEntity.getId(),entry.getKey());
                //如果是一对多，多对多
                if(relationType ==RelationType.OneToMany||relationType == RelationType.ManyToMany){
                    from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append("List").append(" as ").append(unCapOtherEntityName).append(" ");
                }else{
                    from.append(" join ").append(unCapMainEntityName).append(".").append(unCapOtherEntityName).append(" as ").append(unCapOtherEntityName).append(" ");
                }
            }
            for(SearchResult searchResult:entry.getValue()){
                String fieldName;
                if(null == searchResult.getField()) {
                    fieldName = searchResult.getFieldName().split("_")[1];
                }else{
                    fieldName = searchResult.getField().getName();
                }
                select.append(unCapOtherEntityName).append(".").append(fieldName).append(" as ").append(unCapOtherEntityName).append(StringUtil.capFirst(fieldName)).append(",");
            }
        }

        String selectStr = select.toString().substring(0,select.toString().length()-1);

        return new StringBuilder(selectStr).append(")").append(" from ").append(from.toString()).toString();
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

    //获取where 字符串
    private static String getWhere(SearchMethod method) {
        StringBuilder where = new StringBuilder(" where ");
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
                String unCapEntityName = StringUtil.unCapFirst(SearchDBUtil.getEntityName(condition.getFieldName().split("_")[0]));
                String fieldName;
//                if(null == condition.getField()){
//                    fieldName = condition.getFieldName().split("_")[1];
//                }else{
                    fieldName = condition.getField().getName();
                //}
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

}