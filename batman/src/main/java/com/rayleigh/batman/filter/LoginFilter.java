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
        //静态资源不拦截
        if(uri.contains("/css")||uri.contains("/js")||uri.contains("/fonts")||uri.contains("/images")||uri.contains("/pageJs")){
            filterChain.doFilter(request,response);
            return;
        }
        if(!uri.endsWith("/login")&&!uri.endsWith("/register")){
            if(null ==request.getSession().getAttribute("user")){
                //转发
                request.getRequestDispatcher("/login").forward(request,response);
                return;
            }else{
                filterChain.doFilter(request,response);
                return;
            }
        }else{
            filterChain.doFilter(request,response);
            return;
        }

    }
}
