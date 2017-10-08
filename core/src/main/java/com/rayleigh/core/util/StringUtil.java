package com.rayleigh.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangn20 on 2017/7/4.
 */
public class StringUtil {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);


    public static boolean isEmpty(String str){
        if(null==str||str.trim().length()==0){
            return true;
        }else{
            return false;
        }
    }
    //首字母大写
    public static String capFirst(String str){
        return new StringBuilder(str.substring(0,1).toUpperCase()).append(str.substring(1)).toString();
    }
    //判断首字母是不是大写
    public static boolean isCapFirst(String str){
        String first = str.substring(0,1);
        if(first.equals(first.toUpperCase())){
            return true;
        }else{
            return false;
        }
    }
    //首字线小写
    public static String unCapFirst(String str){
        return new StringBuilder(str.substring(0,1).toLowerCase()).append(str.substring(1)).toString();
    }
    //判断首字母是不是小写
    public static boolean isUnCapFirst(String str){
        String first = str.substring(0,1);
        if(first.equals(first.toLowerCase())){
            return true;
        }else{
            return false;
        }
    }
    //时间转化成字符串
    public static String dateToDbString(Date date){
        //将时间格式化，可以放到数据库的字符串
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        return simpleDateFormat.format(date);
    }

    public static String booleanToString(Boolean type){
        if(type){
            return "true";
        }else{
            return "false";
        }
    }
    //Date转化成字符串yyyy-MM-dd hh:mm:ss
    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }
    //字符串yyyy-MM-dd hh:mm:ss 转化成Date类型
    public static Date stringToDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return simpleDateFormat.parse(dateString);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        System.out.println(isCapFirst("test"));
        System.out.println(isCapFirst("12EE"));
        System.out.println(isCapFirst("Eest"));
    }

}
