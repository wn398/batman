package com.rayleigh.core.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * httpClient支持类
 */
public class HttpClientUtil {
    public static String doPost(String url, Map<String, String> headerMap,String bodyJsonStr) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig());
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        for(Map.Entry<String,String> entry:headerMap.entrySet()){
            httpPost.addHeader(entry.getKey(),entry.getValue());
        }
        httpPost.setEntity(new StringEntity(bodyJsonStr, Charset.forName("UTF-8")));
        HttpResponse response = httpClient.execute(httpPost);
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, "utf-8");
            }
        }

        return result;
    }

    public static String doGet(String url,Map<String,String> headerParam) throws Exception{
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(getRequestConfig());
        //httpGet.addHeader("Content-type","application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        String result = null;
        for(Map.Entry<String,String> entry:headerParam.entrySet()){
            httpGet.addHeader(entry.getKey(),entry.getValue());
        }
        HttpResponse response = httpClient.execute(httpGet);
        if(null !=response){
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity !=null){
                result = EntityUtils.toString(responseEntity,"utf-8");
            }
        }
        return result;
    }

    public static String doPut(String url,Map<String,String> headerParam,String jsonBody)throws Exception{
        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(getRequestConfig());
        for(Map.Entry<String,String> entry:headerParam.entrySet()){
            httpPut.addHeader(entry.getKey(),entry.getValue());
        }
        httpPut.addHeader("Content-type","application/json; charset=utf-8");
        httpPut.setHeader("Accept", "application/json");

        httpPut.setEntity(new StringEntity(jsonBody,Charset.forName("UTF-8")));

        HttpResponse response = httpClient.execute(httpPut);
        String result = null;
        if(null !=response){
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity !=null){
                result = EntityUtils.toString(responseEntity,"utf-8");
            }
        }
        return result;
    }

    public static String doDelete(String url,Map<String,String> headerParam)throws Exception{
        HttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(getRequestConfig());
        for(Map.Entry<String,String> entry:headerParam.entrySet()){
            httpDelete.addHeader(entry.getKey(),entry.getValue());
        }
        //httpDelete.addHeader("Content-type","application/json; charset=utf-8");
        httpDelete.setHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(httpDelete);
        String result = null;
        if(null !=response){
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity !=null){
                result = EntityUtils.toString(responseEntity,"utf-8");
            }
        }
        return result;
    }


    public static void main(String[] args) {
//        Map map = new HashMap();
//        map.put("token","eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJwYXNzd29yZCI6InN0cmluZyIsInVzZXJuYW1lIjoic3RyaW5nIiwicmFuZG9tIjotODM5ODA1ODQ5Njk0MTE5NDYyNywiZXhwIjoxNTAzNTQ0MDA5LCJuYmYiOjE1MDM1NDM0MDl9.S_38oT4Hh03KflXIXBe60rpM4q2br5dgMoperIVYcM0");
//        try {
//            String str = doGet("http://localhost:15146/api/studentCtl/findOne/8aa652c65dc658d9015dc66d468b0004", map);
//            System.out.printf(str);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        String bodyStr = "{\n" +
                "    \"isType\": true,\n" +
                "    \"name\": \"测试\"\n" +
                "}";
        Map headerMap = new HashMap();
        headerMap.put("token","eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJwYXNzd29yZCI6InN0cmluZyIsInVzZXJuYW1lIjoic3RyaW5nIiwicmFuZG9tIjotMjA2NzA4MDEzNTQ3MTU5ODIyOSwiZXhwIjoxNTAzNTUyMDExLCJuYmYiOjE1MDM1NDYwMTF9.7F2_dOOfrREUF8ea3Wkym7Ne0JByDIbUzDpCjvtUMQo");
        try{
            String str = doPost("http://localhost:15146/api/dictCtl/doSaveOrUpdate",headerMap,bodyStr);
            System.out.println(str);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static RequestConfig getRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(10000).build();
        return requestConfig;
    }
} 