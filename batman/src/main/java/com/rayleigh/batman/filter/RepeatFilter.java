package com.rayleigh.batman.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@WebFilter(filterName = "repeatFilter",urlPatterns = "/*")
public class RepeatFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(RepeatFilter.class);

    private static List<String> repeatUrlList;

    private static String rootPath;

    private static Cache<String,String> cache = CacheBuilder.newBuilder().expireAfterWrite(1500,TimeUnit.MILLISECONDS).build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        if(null==repeatUrlList){
            loadRepeatUrl();
        }
        if(null == rootPath){
            getRootPath(url);
        }
        String subUrl = url.substring(url.indexOf("/",1));
        if(repeatUrlList.contains(subUrl)){
            String ip = getIpAddr(request);
            if(null != cache.asMap().get(ip)) {
                ResultWrapper resultWrapper = new ResultWrapper();
                resultWrapper.setStatus(ResultStatus.NOT_VALID);
                resultWrapper.setInfo("请不要重复提交数据!");
                logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求url:").append(url).append(" 重复提交数据，被拦截").toString());
                getHttpServletResponse(response).getWriter().write(JSONObject.toJSONString(resultWrapper));
                cache.put(ip,subUrl);
                return;
            }else{
                cache.put(ip,subUrl);
            }
        }
        filterChain.doFilter(request,response);

    }

    private synchronized void getRootPath(String url){
        logger.info("初始化rootPath");
        if(null == rootPath){
            rootPath = url.split("/")[1];
        }else{
            return;
        }
    }

    private synchronized void loadRepeatUrl(){
        logger.info("初始化repeatUrl");
        if(null==repeatUrlList){
            repeatUrlList = new ArrayList<>();
        }else{
            return;
        }
        Properties properties = new Properties();
        try{
            properties.load(getClass().getResourceAsStream("/repeat.properties"));
            String urls = properties.getProperty("repeat.urls");
            for(String url:urls.split(",")){
                if(!StringUtil.isEmpty(url)){
                    repeatUrlList.add(url);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("加载repeatUrl出错!");
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-Real-IP");
            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("X-Forwarded-For");
            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
        } catch (Exception e) {
            logger.error("获取IP地址异常", e);
        }
        return request.getRemoteAddr();
    }

    //获取HttpServletResponse
    private HttpServletResponse getHttpServletResponse(HttpServletResponse servletResponse) {
        HttpServletResponse httpResponse = servletResponse;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return httpResponse;
    }


    public static void main(String[] args) {
        Cache<String,String> cache = CacheBuilder.newBuilder().expireAfterWrite(2,TimeUnit.SECONDS).build();
        cache.put("1","122");
        cache.put("1","2323");
        System.out.println(cache.asMap().get("1"));
        try{
            Thread.sleep(2000L);
        }catch (Exception e){

        }
        System.out.println("第二次获取:"+cache.asMap().get("1"));

    }
}
