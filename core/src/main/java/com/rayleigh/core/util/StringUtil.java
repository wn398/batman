package com.rayleigh.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangn20 on 2017/7/4.
 */
public class StringUtil {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);
    public static final char UNDERLINE = '_';

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

    public static boolean isDigitFirst(String str){
        if(null!=str && str.length()>0){
            return Character.isDigit(str.charAt(0));
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
    //字符串yyyy-MM-dd hh:mm:ss 转化成Date类型
    public static Date stringToDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(dateString);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //字符串转换成MD5
    public static String stringToMD5(String str) throws NoSuchAlgorithmException,UnsupportedEncodingException {
        // 生成一个MD5加密计算摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 计算md5函数
        byte[] bytes = md.digest(str.getBytes("utf-8"));
        String result = new BigInteger(1, bytes).toString(16);
        return result;
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


    public static void main(String[] args) {
//        System.out.println(isCapFirst("test"));
//        System.out.println(isCapFirst("12EE"));
//        System.out.println(isCapFirst("Eest"));
        System.out.println(humpToUnderline("updateDate"));
    }

}
