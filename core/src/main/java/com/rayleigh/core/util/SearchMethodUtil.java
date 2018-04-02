package com.rayleigh.core.util;

import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.enums.Operation;
import com.rayleigh.core.model.SearchMethodConditionModel;
import com.rayleigh.core.model.SearchMethodResultModel;

import java.util.*;
import java.util.stream.Collectors;

public class SearchMethodUtil {
    //通过提供的条件列表，构建对应的查询条件字符串
    public static String getDynamicConditionStr(List<SearchMethodConditionModel> conditionModelList){
        StringBuilder where = new StringBuilder();
        //根据查询条件的优先级排序
        Map<Integer,List<SearchMethodConditionModel>> groupByPriorityMap = conditionModelList.parallelStream().collect(Collectors.groupingBy(SearchMethodConditionModel::getPriority));

        List<Map.Entry<Integer,List<SearchMethodConditionModel>>> list = new ArrayList<>(groupByPriorityMap.entrySet());
        //如果存在多个级别就进行排序
        if (groupByPriorityMap.size() > 1){
            Collections.sort(list, new Comparator<Map.Entry<Integer, List<SearchMethodConditionModel>>>() {
                @Override
                public int compare(Map.Entry<Integer, List<SearchMethodConditionModel>> o1, Map.Entry<Integer, List<SearchMethodConditionModel>> o2) {
                    return o2.getKey() - o1.getKey();
                }
            });
        }
        //处理最外层条件
        List<String> conditions = new ArrayList<>();
        for(Map.Entry<Integer,List<SearchMethodConditionModel>> entry:list){
            //当前遍历级别下的逻辑操作符
            LogicOperation logicOperation =getLogicOperationFromCondition(entry.getValue());
            //里面的条件
            List<String> subCondition = new ArrayList<>();
            //对一个级别进行处理
            for(SearchMethodConditionModel condition:entry.getValue()){
                //操作类型
                Operation operation = condition.getOperation();
                String unCapEntityName = StringUtil.unCapFirst(condition.getEntityName());
                String fieldName;
//                if(null == condition.getField()){
//                    fieldName = condition.getFieldName().split("_")[1];
//                }else{
                fieldName = condition.getFieldName();
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
        return  where.toString();
    }

    //从一组条件里提取逻辑操作符
    public static LogicOperation getLogicOperationFromCondition(List<SearchMethodConditionModel> conditions){
        Map<LogicOperation,List<SearchMethodConditionModel>> map = conditions.parallelStream().collect(Collectors.groupingBy(condition->condition.getLogicOperation()));
        if(map.size()!=1){
            throw new RuntimeException("逻辑运算符在同一个优先级里相互不相同！！conditionName "+conditions.parallelStream().map(condition->condition.getConditionName()).collect(Collectors.joining(",")));
        }else{
            return (LogicOperation) map.keySet().toArray()[0];
        }

    }

    //获取orderBy查询字符串（排序 现在按结果的类型和优先级排序,优先级默认为0，表示没有设置优先级，级数越高，越优先）
    public static String getOrderByStr(List<SearchMethodResultModel> searchMethodResultModelList){
        StringBuilder orderBy = new StringBuilder(" order by ");
        List<String> orderByList = new ArrayList<>();
        //过滤出非0的排序result
        if(null!=searchMethodResultModelList&&searchMethodResultModelList.size()>0){
            searchMethodResultModelList.parallelStream().collect(Collectors.groupingBy(searchResult->searchResult.getOrderByNum()));
            //倒序排列
            Collections.sort(searchMethodResultModelList, new Comparator<SearchMethodResultModel>() {
                @Override
                public int compare(SearchMethodResultModel o1, SearchMethodResultModel o2) {
                    return o2.getOrderByNum()-o1.getOrderByNum();
                }
            });

            for(SearchMethodResultModel searchMethodResultModel:searchMethodResultModelList){
                String entityName = searchMethodResultModel.getEntityName();
                String fieldName = searchMethodResultModel.getFieldName();
                String orderByType =searchMethodResultModel.getOrderByType().toString();
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
