package com.rayleigh.batman.util;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.batman.model.Project;
import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneratorStringUtil {
    public static final char UNDERLINE = '_';
    private static Logger logger = LoggerFactory.getLogger(GeneratorStringUtil.class);

    public static boolean isEmpty(String str){
        if(null!=str&&str.trim().length()>0){
            return true;
        }else{
            return false;
        }
    }

    public static String constructInsertSql(Project project, Entities entities){
        StringBuilder sb = new StringBuilder("\"insert into ");
        if(!StringUtil.isEmpty(entities.getTableName())){
            sb.append(entities.getTableName());
        }else{
            if(!StringUtil.isEmpty(entities.getPreFix())){
                sb.append(entities.getPreFix()).append("_").append(humpToUnderline(entities.getName()));
            }else{
                if(entities.getAddPrefix()){
                    sb.append(humpToUnderline(entities.getModule().getName()+entities.getName()));
                }else{
                    sb.append(humpToUnderline(entities.getName()));
                }
            }
        }
        List<String> dbFieldsNames = new ArrayList<>();
        List<String> fieldValues = new ArrayList<>();
        String unCapEntityName = StringUtil.unCapFirst(entities.getName());
        //处理id,createDate,updateDate
        //id ，createDate,updateDate放在field中所以去掉特殊处理
//        dbFieldsNames.add("id");
//        fieldValues.add(new StringBuilder("'\"").append("+").append(unCapEntityName).append(".getId()").append("+").append("\"'").toString());
//        dbFieldsNames.add("create_date");
//        fieldValues.add(new StringBuilder("'\"").append("+").append(unCapEntityName).append(".getCreateDate()").append("+").append("\"'").toString());
//        dbFieldsNames.add("update_date");
//        fieldValues.add(new StringBuilder("'\"").append("+").append(unCapEntityName).append(".getUpdateDate()").append("+").append("\"'").toString());
//        dbFieldsNames.add("version");
//        fieldValues.add(new StringBuilder("\"").append("+").append(0).append("+").append("\"").toString());
        //处理dbFieldName和value
        for(Field field:entities.getFields()){
            dbFieldsNames.add(humpToUnderline(field.getName()));
            if(field.getDataType()== DataType.String) {
                fieldValues.add(new StringBuilder("'\"").append("+").append(unCapEntityName).append(".get").append(StringUtil.capFirst(field.getName())).append("()").append("+").append("\"'").toString());
            }else if(field.getDataType() == DataType.Date){
                fieldValues.add(new StringBuilder("'\"").append("+").append("StringUtil.dateToDbString(").append(unCapEntityName).append(".get").append(StringUtil.capFirst(field.getName())).append("()").append(")").append("+").append("\"'").toString());
            }else if(field.getDataType() == DataType.Integer || field.getDataType() == DataType.Double || field.getDataType() == DataType.BigDecimal){
                fieldValues.add(new StringBuilder("\"").append("+").append(unCapEntityName).append(".get").append(StringUtil.capFirst(field.getName())).append("()").append("+").append("\"").toString());
            }else if(field.getDataType() == DataType.Boolean){
                fieldValues.add(new StringBuilder("\"").append("+").append("StringUtil.booleanToString(").append(unCapEntityName).append(".get").append(StringUtil.capFirst(field.getName())).append("())").append("+").append("\"").toString());
            }
        }

        sb.append(" (").append(dbFieldsNames.stream().collect(Collectors.joining(","))).append(") values (").append(fieldValues.stream().collect(Collectors.joining(","))).append(")\"");
        return sb.toString();
    }

    //驼峰转下划线，如（BatMan==>bat_man)
    public static String humpToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                //首字母大写，只是转化为小写
                if(i>0) {
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //下划线转驼峰
    public static String underlineToHump(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }
    //将两个字符串由驼峰转换成下划线形式，并按升序排序
    public static String  humpToUnderlineAndOrder(String str1,String str2){
        String str11 = humpToUnderline(str1);
        String str22 = humpToUnderline(str2);
        if(str11.compareTo(str22)<0){
            return new StringBuilder(str11).append("_").append(str22).toString();
        }else{
            return new StringBuilder(str22).append("_").append(str11).toString();
        }
    }

    public static void main(String[] args) {
        logger.info(humpToUnderline("BatManTest"));
        logger.info(underlineToHump("bat_man_test"));
    }
    //首字母大写
    public static String upperFirstLetter(String name) {
        return new StringBuilder().append(name.substring(0,1).toUpperCase()).append(name.substring(1)).toString();
    }

    public static String toLowerCase(String str){
        if(null!=str){
            return str.toLowerCase();
        }else{
            return null;
        }
    }

    public static String toUpperCase(String str){
        if(null!=str){
            return str.toUpperCase();
        }else {
            return null;
        }
    }

    public static String number2String(Integer arg){
        return String.valueOf(arg);
    }
}
