package com.rayleigh.batman.filter;

import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@WebFilter(filterName = "loginFilter",urlPatterns = "/*")
public class LoginFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static List<String> loginUrlList;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if(uri.contains("/css")||uri.contains("/js")||uri.contains("/fonts")||uri.contains("/images")||uri.contains("/pageJs")||uri.contains("codeGeneratorCtl")){
            filterChain.doFilter(request,response);
            return;
        }
        if(null==loginUrlList){
            loadRepeatUrl();
        }
        for(String str:loginUrlList){
            if(uri.contains(str)){
                filterChain.doFilter(request,response);
                return;
            }
        }

        String userId = (String)request.getSession().getAttribute("userId");
        if(null == userId){
            if(uri.endsWith("login")||uri.endsWith("register")||uri.endsWith("js")||uri.contains(".css")||uri.endsWith("ico")||uri.endsWith("jpg")||uri.endsWith("png")||uri.contains("/fonts/")){
                filterChain.doFilter(request,response);
                return;
            }else {
                String type = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(type)) {
                    // ajax请求
                    String root = request.getRequestURI().split("/")[1];
                    response.setHeader("SESSIONSTATUS", "TIMEOUT");
                    response.setHeader("CONTEXTPATH", new StringBuilder("/").append(root).append("/").append("login").toString());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;

                } else {
                    //重定向
                    String root = request.getRequestURI().split("/")[1];
                    response.sendRedirect(new StringBuilder("/").append(root).append("/").append("login").toString());
                    //request.getRequestDispatcher("login").forward(request,response);
                    return;
                }
            }
        }else{
            filterChain.doFilter(request,response);
            return;
        }
    }



    private synchronized void loadRepeatUrl(){
        logger.info("初始化loginExclude");
        if(null==loginUrlList){
            loginUrlList = new ArrayList<>();
        }else{
            return;
        }
        Properties properties = new Properties();
        try{
            properties.load(getClass().getResourceAsStream("/loginExclude.properties"));
            String urls = properties.getProperty("exclude.urls");
            for(String url:urls.split(",")){
                if(!StringUtil.isEmpty(url)){
                    loginUrlList.add(url);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("加载loginExclude出错!");
        }
    }
}
