package com.rayleigh.batman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test2 {
    static Logger logger = LoggerFactory.getLogger(Test2.class);
    //获取第一天的字符串
    private static String getFirstDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return dayAdd(sdf.format(new Date()),getNeedDay()*-1);
    }

    //获取当前时间往前需要统计的天数
    private static Integer getNeedDay(){
        int nowWeekDay = new Date().getDay();
        int needDays = 0;
        if(0!=nowWeekDay){
            needDays = 14+nowWeekDay;
        }else{
            needDays = 21;
        }
        return needDays-1;
    }

    private static String dayAdd(String day, Integer i){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(simpleDateFormat.parse(day));
            rightNow.add(Calendar.DAY_OF_YEAR, i);//日期加1天
            Date dt1 = rightNow.getTime();
            String reStr = simpleDateFormat.format(dt1);

            return reStr;
        }catch (Exception e){
            logger.error("格式化日期错误！");
        }
        return  null;
    }

    public static void main(String[] args) {
//        String llweekStart = new Test2().getFirstDay();
//        String lweekStart = dayAdd(llweekStart,7);
//        String weekStart = dayAdd(lweekStart,7);
//        System.out.println(llweekStart);
//        System.out.println(lweekStart);
//        System.out.println(weekStart);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add(0,"0");
        list.add(0,"4");
        System.out.println(list);

    }
}
