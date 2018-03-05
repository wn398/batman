package com.rayleigh.batman.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginFilter",urlPatterns = "/*")
public class LoginFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if(uri.contains("/css")||uri.contains("/js")||uri.contains("/fonts")||uri.contains("/images")||uri.contains("/pageJs")||uri.contains("codeGeneratorCtl")){
            filterChain.doFilter(request,response);
            return;
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
}
