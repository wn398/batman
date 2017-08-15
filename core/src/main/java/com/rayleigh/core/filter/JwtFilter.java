package com.rayleigh.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@WebFilter(filterName = "jwtFilter", urlPatterns = "/*")
public class JwtFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    //是否开启了jwt
    @Value("${jwt.enabled}")
    private boolean isEnable;
    @Value("${jwt.secret.key}")
    private String base64Key;
    @Value("${jwt.exclude.urls}")
    private String[] excludes;
    @Value("${jwt.noPermission.page.url}")
    private String noPermissionPageUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化:jwtFilter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isEnable) {

            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String url = httpRequest.getRequestURI();
            String auth = httpRequest.getHeader("token");
//            if(null == auth){
//                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//                httpResponse.setCharacterEncoding("UTF-8");
//                httpResponse.setContentType("application/json; charset=utf-8");
//                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                ResultWrapper resultWrapper = new ResultWrapper();
//                resultWrapper.setStatus(ResultStatus.INVALID_TOKEN);
//                resultWrapper.setInfo("请在header里加入token!");
//                httpResponse.getWriter().write(JSONObject.toJSONString(resultWrapper));
//                return;
//            }

            Claims claims = JWTUtil.parseJWT(auth, base64Key);
            if (isExcludeUri(url) || (null != claims && claims.getExpiration().compareTo(new Date()) > 0)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                ResultWrapper resultWrapper = new ResultWrapper();
                if (null == claims) {
                    resultWrapper.setStatus(ResultStatus.INVALID_TOKEN);
                    logger.info(new StringBuilder("请求:").append(url).append(" 未知token:[").append(auth).append("] 被拦截！").toString());
                    resultWrapper.setInfo("未知token,请在");
                } else {
                    resultWrapper.setStatus(ResultStatus.EXPIRED_TOKEN);
                    logger.info(new StringBuilder("请求:").append(url).append(" 过期token:[").append(auth).append("] 被拦截").toString());
                    resultWrapper.setInfo("过期token,请检查");
                }
                //ajax请求
                String requestType = httpRequest.getHeader("X-Requested-With");
                if (null != requestType) {
                    httpResponse.getWriter().write(JSONObject.toJSONString(resultWrapper));
                    return;
                } else {
                    httpRequest.getRequestDispatcher(noPermissionPageUrl).forward(httpRequest, httpResponse);
                }
                return;
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

    }

    @Override
    public void destroy() {

    }

    //是否排除的uri
    private boolean isExcludeUri(String uri) {
        //排除主页，根目录
        if (uri.endsWith("/") || uri.equals(noPermissionPageUrl)) {
            return true;
        }
        for (String publicUri : excludes) {
            if (uri.contains(publicUri.trim())) {
                return true;
            }
        }
        return false;
    }
}
