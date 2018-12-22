package com.rayleigh.batman.util;

import com.rayleigh.batman.model.BatmanBaseModel;
import com.rayleigh.core.exception.NotBaseModelException;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.util.SpringContextUtils;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangn20 on 2017/7/17.
 */
public class BatmanBaseModelUtil {
    private final static Logger logger = LoggerFactory.getLogger(BatmanBaseModelUtil.class);


    /**去掉相互引用，防止生成JSON失败,传入list作为存储已经存在的对象
     *
     * @param baseModel
     * @param blankList
     */
    public static void preventMutualRef(BatmanBaseModel baseModel, List blankList){

        blankList.add(baseModel);

        Class clazz = baseModel.getClass();
        //获取所有属性
        Field[] fields = clazz.getDeclaredFields();

        for(Field field:fields){
            if(field.getName().startsWith("_")||field.getName().equals("serialVersionUID")){
                continue;
            }
            Object value = invokeFieldGetMethod(baseModel,field);
            if(value instanceof BaseModel){
                    if(blankList.contains(value)){
                        invokeFiledSetMethod(baseModel,field,null);
                    }else{
                        blankList.add(value);
                        preventMutualRef((BatmanBaseModel) value,blankList);
                    }
            }else if(value instanceof List){
                //需要删除的list
                List needRemoveList = new ArrayList();
                for(Object obj:(List)value){
                    if(obj instanceof BaseModel){
                        if(blankList.contains(obj)){
                            //invokeFiledSetMethod(baseModel,field,null);
                            needRemoveList.add(obj);
                        }else{
                            blankList.add(obj);
                            preventMutualRef((BatmanBaseModel) obj,blankList);
                        }
                    }
                }
                //遍历完之后删除
                for(Object obj:needRemoveList){
                    ((List) value).remove(obj);
                }
            }

        }
    }


    //处理保存或更新一个物体前，使对应的对象转换成持久化状态
    public static Object saveOrUpdateBaseModelObjWithRelationPreProcess(Object baseModelOrBaseModelList) throws NotBaseModelException{
        if(baseModelOrBaseModelList instanceof BaseModel){
            //id不为空,则从数据库中加载出持久化对象出来，把需要修改的属性copy过去
            if(!StringUtil.isEmpty(((BatmanBaseModel) baseModelOrBaseModelList).getId())){
                BatmanBaseModel dbBaseModel = getDBObjectFromCommonBaseModel((BatmanBaseModel) baseModelOrBaseModelList);
                //检查version,如果version不为空，则是更新，否则是设置关系，直接使用数据库里的就可以  【2017-07-24，新增逻辑】
                if(null!=((BatmanBaseModel) baseModelOrBaseModelList).getVersion()) {
                    copyProperty((BatmanBaseModel) baseModelOrBaseModelList, dbBaseModel);

                }
                //使数据库中对象，指向原来，防止在属性中设置之后，仍然使用原来的指向【2017-07-26加】
                baseModelOrBaseModelList = dbBaseModel;
                return baseModelOrBaseModelList;
            }else{
                //如果id为空，则说明是新增对象，并处理对应的关系对象从数据中加载出来
                saveComplexBaseModelWithRelationPreProcess((BatmanBaseModel) baseModelOrBaseModelList);
                return baseModelOrBaseModelList;
            }
        }else if(baseModelOrBaseModelList instanceof List){
            List resultList = new ArrayList();
            for(Object objItem:(List)baseModelOrBaseModelList){
                if(objItem instanceof BaseModel){
                    resultList.add(saveOrUpdateBaseModelObjWithRelationPreProcess(objItem));
                }else{
                    throw new NotBaseModelException("所处理对象包含非BaseModel类型:"+objItem.toString());
                }
            }
            return resultList;
        }else{
            throw new NotBaseModelException("所处理对象包含非BaseModel类型:"+baseModelOrBaseModelList.toString());
        }
    }

    //保存对象时，如果关系对象传入id，在对象中只存在id，直接保存会报错，需要转换成持久化状态(从数据库查询出来),此方法传入参数是主对象id没有设置的，使它的关联对象转化为持久化对象
    private static void saveComplexBaseModelWithRelationPreProcess(BatmanBaseModel source){
        Class clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for(Field field:fields){
            if(field.getName().startsWith("_")||field.getName().equals("SerialVersionUID")){
                continue;
            }
            Object fieldValue = invokeFieldGetMethod(source,field);

            if(fieldValue instanceof BaseModel){
                if(!StringUtil.isEmpty(((BatmanBaseModel) fieldValue).getId())){
                    BatmanBaseModel dbFieldValue = getDBObjectFromCommonBaseModel((BatmanBaseModel) fieldValue);
                    //copy基本属性过去，应对有可能的修改，并不是只有id为空
                    copyBasicProperty((BatmanBaseModel) fieldValue,dbFieldValue);
                    invokeFiledSetMethod(source,field,dbFieldValue);
                }
            }else if(fieldValue instanceof List){
                List resultList = new ArrayList();
                for(Object listFiledItemValue:(List)fieldValue){
                    if(listFiledItemValue instanceof BaseModel){
                        //存在id的为更新
                        if(!StringUtil.isEmpty(((BatmanBaseModel) listFiledItemValue).getId())){
                            BatmanBaseModel dbListFiledItemValue = getDBObjectFromCommonBaseModel((BatmanBaseModel) listFiledItemValue);
                            copyBasicProperty((BatmanBaseModel) listFiledItemValue,dbListFiledItemValue);
                            resultList.add(dbListFiledItemValue);
                        }else{
                            //不存在id的是新增
                            resultList.add(listFiledItemValue);
                        }
                    }
                }
                //用处理后的list重新设置list
                invokeFiledSetMethod(source,field,resultList);
            }
        }
    }

    //仅copy两层对象类型的,在更新对象时，作为传入的对象source,target是从数据库查询出来的对象，根据传入的子属性对象id,会自动转换成数据库查询出来的持久化对象设置进去
    private static void copyProperty(BatmanBaseModel source,BatmanBaseModel target){

        Class clazz = source.getClass();
        //获取所有属性
        Field[] fields = clazz.getDeclaredFields();

        for(Field field:fields){
            if(field.getName().startsWith("_")||field.getName().equals("SerialVersionUID")){
                continue;
            }
            Object sourceFieldValue = invokeFieldGetMethod(source,field);
            Object targetFieldValue = invokeFieldGetMethod(target,field);
            if(null!=sourceFieldValue){
                //如果即不是baseMode，也不是List，说明是普通类型
                if(!(sourceFieldValue instanceof BaseModel)&&!(sourceFieldValue instanceof List)){
                    //把不为null的源属性，调到到目标对象中
                    invokeFiledSetMethod(target,field,sourceFieldValue);
                }else if(sourceFieldValue instanceof BaseModel){

                    if(null!=((BatmanBaseModel) sourceFieldValue).getId()&&null!=targetFieldValue&&((BatmanBaseModel) sourceFieldValue).getId().equals(((BatmanBaseModel)targetFieldValue).getId())) {
                        //如果二者的id相同，则去更新对应的简单属性
                        copyBasicProperty((BatmanBaseModel) sourceFieldValue, (BatmanBaseModel) targetFieldValue);
                    }else if((!StringUtil.isEmpty(((BatmanBaseModel) sourceFieldValue).getId())&&!((BatmanBaseModel) sourceFieldValue).getId().equals(((BatmanBaseModel)targetFieldValue).getId()))){
                        //如果源id不为空且不等于目标的id,说明是建立新关系,或者并且更新，就直接从数据库查出来设置设置属性
                        BatmanBaseModel dbModelValue = getDBObjectFromCommonBaseModel((BatmanBaseModel) sourceFieldValue);
                        copyBasicProperty((BatmanBaseModel) sourceFieldValue,dbModelValue);
                        invokeFiledSetMethod(target,field, dbModelValue);
                    }else{
                        //其它情况不做处理
                    }
                }else if(sourceFieldValue instanceof List){
                    List resultList = new ArrayList();
                    for(BatmanBaseModel sourceSubValue:(List<BatmanBaseModel>)sourceFieldValue){
                        if(!StringUtil.isEmpty(sourceSubValue.getId())){
                            for(BatmanBaseModel targetSubValue:(List<BatmanBaseModel>)targetFieldValue){

                                if(sourceSubValue.getId().equals(targetSubValue.getId())){
                                    //找到list中相对应的对象，作copy基本属性更新
                                    copyBasicProperty(sourceSubValue,targetSubValue);
                                    //copyProperty(sourceSubValue,targetSubValue,list);
                                    resultList.add(targetSubValue);
                                }
                            }
                        }else{
                            //如果源中没有设置id，说明是新增的list中的项
                            resultList.add(sourceSubValue);
                        }
                    }
                    //把目标里的list先清除，再重新加载上最新的
                    ((List)targetFieldValue).clear();
                    ((List)targetFieldValue).addAll(resultList);
                }
            }
        }
    }

    /**
     * 调用对应field的set方法
     * @Param object 调用的对象
     * @Param field 对象的属性
     * @Param methodParameter 方法参数
     */
    private static void invokeFiledSetMethod(Object object,Field field,Object methodParameter){
        try{
             getSetMethodByField(object.getClass(),field).invoke(object,methodParameter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //调用对应filed的get方法
    private static Object invokeFieldGetMethod(Object object,Field field){
        try {
            return getGetMethodByField(object.getClass(), field).invoke(object);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 通过filed获取对应get方法
     * @Param clazz 方法所在的类
     * @field get方法对应的属性
     */

    private static Method getGetMethodByField(Class clazz,Field field ){
        String methodName = new StringBuilder("get").append(field.getName().substring(0,1).toUpperCase()).append(field.getName().substring(1)).toString();
        try {
           // logger.info(clazz.getName()+"."+methodName);
            return clazz.getMethod(methodName);
        }catch (Exception e){
            logger.info(methodName+"不存在于类："+clazz.getSimpleName());
            return null;
        }

    }

    //通过filed获取对应set方法
    private static Method getSetMethodByField(Class clazz,Field field ){
        String methodName = new StringBuilder("set").append(field.getName().substring(0,1).toUpperCase()).append(field.getName().substring(1)).toString();
        try {
            return clazz.getMethod(methodName,field.getType());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**获取一个对象在数据库的对象状态,通过id获取到对象的持久化状态，要求传入的对象id必须存在
     *
     * @param baseModel
     * @return
     */
    private static BatmanBaseModel getDBObjectFromCommonBaseModel(BatmanBaseModel baseModel){
        Class<? extends BatmanBaseModel> clazz =baseModel.getClass();
        String name = clazz.getSimpleName();
        //获取Repository
        String repositoryName = new StringBuilder(name.substring(0,1).toLowerCase()).append(name.substring(1)).append("Repository").toString();
        BatmanBaseModel databaseModel = (BatmanBaseModel) ((CrudRepository) SpringContextUtils.getBean(repositoryName)).findById(baseModel.getId()).get();
        return  databaseModel;
    }

    /**仅copy基本的属性，不会copy属性为对象和list的，因为一般业务都只是两层模型
     *
     * @param source  源对象
     * @param target  目标对象
     */
    private static void copyBasicProperty(BatmanBaseModel source,BatmanBaseModel target){
        Class clazz = source.getClass();
        //获取所有属性
        Field[] fields = clazz.getDeclaredFields();

        for(Field field:fields){
            if(field.getName().startsWith("_")||field.getName().equals("SerialVersionUID")){
                continue;
            }
            Object sourceValue = invokeFieldGetMethod(source,field);
            Object targetValue = invokeFieldGetMethod(target,field);
            if(null!=sourceValue){
                if(!(sourceValue instanceof BaseModel) && !(sourceValue instanceof List)){
                    invokeFiledSetMethod(target,field,sourceValue);
                }
            }
        }
    }

}
