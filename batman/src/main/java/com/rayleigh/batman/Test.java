package com.rayleigh.batman;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/19.
 */
public class Test {

    public static void main(String[] args) throws Exception{
//        Path path = Paths.get("C:\\Users\\wangn20\\Downloads\\AdminLTE-2.3.11 (2)\\AdminLTE-2.3.11");
//
//        Paser(path);
        //this.getClass().getClassLoader().get
        //testMap();
//        List<String> list = new ArrayList<>();
//        list.add("teset");
//        System.out.println(list.parallelStream().collect(Collectors.joining(",")));
////testLink();
        Map map = new HashMap();
        System.out.println(null == map.get("test"));

        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        System.out.println(list.subList(0,3).parallelStream().collect(Collectors.joining(",")));

    }


    public static void testLink(){
        List<String> list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        String str = list.stream().collect(Collectors.joining(" and "));
        System.out.println(str);

    }
    public static void testMap(){
        Map<Integer, String> map = new TreeMap<>();
        map.put(1,"a");
        map.put(3,"c");
        map.put(5,"e");
        map.put(2,"b");


        for(Map.Entry<Integer,String> entry:map.entrySet()){
                System.out.println("key"+entry.getKey()+"-->"+entry.getValue());
        }
    }

    private static void Paser(Path path) throws Exception {
        if(Files.isDirectory(path)) {
            Files.list(path).forEach(
                    it->{
                        try{Paser(it);}
                    catch (Exception e){

                    }});
        }else{
            if (path.toFile().getName().endsWith("html")) {
                try {
                    System.out.print(path.toFile().getName());
                    Files.lines(path).forEach(str -> str.replace("<html>", "<html xmlns:th=\"http://www.thymeleaf.org\">"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
