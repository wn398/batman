package com.rayleigh.core.filter;

import com.alibaba.fastjson.JSON;
import com.rayleigh.core.model.AccessLog;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebFilter(filterName = "TraceFilter", urlPatterns = "/*")
public class TraceFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
        LOGGER.info("初始化LoggerFilter过滤器");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String traceId = httpServletRequest.getHeader("traceId");
        if (StringUtil.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        //String user = SecurityUtils.getCurrentUserLogin();
        String ip = getIpAddr(httpServletRequest);
        String path = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        MDC.put("traceId", traceId);
        //MDC.put("user", user);
        MDC.put("ip", ip);
        MDC.put("path", path);
        MDC.put("method", method);
        LOGGER.debug("用户请求跟踪ID: {}, IP: {}, 请求路径: {}, 请求方式: {}", traceId, ip, path, method);
        AccessLog accessLog = new AccessLog();
        try {
            accessLog.setStart(System.currentTimeMillis());
            httpServletResponse.addHeader("traceId", traceId);
           // httpServletResponse.addHeader("user", user);
            httpServletResponse.addHeader("ip", ip);
            httpServletResponse.addHeader("path", path);
            httpServletResponse.addHeader("method", method);
            chain.doFilter(httpServletRequest, httpServletResponse);
            accessLog.setSuccess("true");
            accessLog.setEnd(System.currentTimeMillis());
        } catch (Throwable t) {
            accessLog.setSuccess("false");
            StringWriter cause = new StringWriter();
            t.printStackTrace(new PrintWriter(cause));
            accessLog.setCause(cause.toString());
            accessLog.setParameters(getParameters(httpServletRequest));
            accessLog.setHeaders(getHeadersInfo(httpServletRequest));
            accessLog.setQueryString(httpServletRequest.getQueryString());
            accessLog.setEnd(System.currentTimeMillis());
            throw t;
        } finally {
            LOGGER.info("处理请求耗时: {}, success: {}, cost: {} ms", accessLog.getCost(), accessLog.getSuccess(), accessLog.getCost());
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("access log \n{}", JSON.toJSONString(accessLog, true));
            }
            //RabbitQueueService rabbitQueueService = SpringContextUtil.getBean("rabbitQueueService");
            //if (rabbitQueueService != null) {
            //    rabbitQueueService.send(RabbitQueue.ACCESS_LOG_QUEUE, JSON.toJSONString(accessLog));
            //}
            MDC.put("traceId", null);
            MDC.put("user", null);
            MDC.put("ip", null);
            MDC.put("path", null);
            MDC.put("method", null);
        }
    }

    private String getParameters(HttpServletRequest httpServletRequest){
        try{
            return JSON.toJSONString(httpServletRequest.getParameterMap());
        }catch (Throwable t){
            return "";
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
            LOGGER.error("获取IP地址异常", e);
        }
        return request.getRemoteAddr();
    }

    public static String getHeadersInfo(HttpServletRequest request) {
        try {
            Map<String, String> map = new HashMap<>();
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
            return JSON.toJSONString(map);
        }catch (Throwable t){
            return "";
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("销毁LoggerFilter过滤器");
    }
}