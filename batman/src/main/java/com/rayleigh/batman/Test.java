package com.rayleigh.batman;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
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
//        Map map = new HashMap();
//        System.out.println(null == map.get("test"));
//
//        List<String> list = new ArrayList<>();
//        list.add("a");
//        list.add("b");
//        list.add("c");
//        list.add("d");
//        list.add("e");
//        System.out.println(list.subList(0,3).parallelStream().collect(Collectors.joining(",")));

        InetAddress inetAddress = getLocalHostLANAddress();
        System.out.println(inetAddress.getHostAddress());
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

    public static InetAddress getLocalHostLANAddress() throws Exception {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
