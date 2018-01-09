package com.rayleigh.core.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Base64Util {
    //获取md5
    public static String getMD5(String str) throws NoSuchAlgorithmException,UnsupportedEncodingException{

            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            String result = new BigInteger(1, bytes).toString(16);
            return result;
    }

    //获取用base64加密的md5字符串
    public static String getBase64Md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        return base64en.encode(md5.digest(str.getBytes("utf-8")));
    }

    //获取用base64编码的字符串
    public static String getBase64(String str)  {
        try {
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            return base64en.encode(str.getBytes("utf-8"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取用base64编码的解密字符串
    public static String getDeBase64(String str){
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            //解密
            return new String(base64Decoder.decodeBuffer(str), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) throws Exception{
        String string = getBase64("vanke@wang20");
        System.out.println(string);
        String str2 = getDeBase64(string);
        System.out.println(str2);

    }

}  