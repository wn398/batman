package ${basePackage}.filter;

import com.alibaba.fastjson.JSONObject;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@WebFilter(filterName = "jwtFilter", urlPatterns = "/*")
@Component
public class JwtFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private boolean isEnable;

    private String base64Key;

    private String[] excludes;

    private String noPermissionPageUrl;

    private final static Map<String, List<String>> roleUserNamePassword = new HashMap<>();
    private final static Map<String, List<String>> roleVisitedUrl = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化:jwtFilter");
        Properties propFlies = new Properties();
        Properties properties2 = new Properties();
        try {
            try (InputStream inputStream = getClass().getResourceAsStream("/permission.properties")) {
                propFlies.load(inputStream);
            }
            String roles = propFlies.getProperty("role");
            for (String role : roles.split(",")) {
                String userNamePassword = propFlies.getProperty(role + ".username_password");
                logger.info("加载角色 " + role + " 用户名密码:" + userNamePassword);
                if (null != userNamePassword) {
                    roleUserNamePassword.put(role, Arrays.asList(userNamePassword.split(",")));
                }
                String visited_url = propFlies.getProperty(role + ".visited_url");
                logger.info("加载角色 " + role + " 权限url:" + visited_url);
                if (null != visited_url) {
                    roleVisitedUrl.put(role, Arrays.asList(visited_url.split(",")));
                }
            }
            try (InputStream inputStream = getClass().getResourceAsStream("/application.properties")) {
                properties2.load(inputStream);
            }
            String active = (String) properties2.get("spring.profiles.active");
            properties2.clear();
            try (InputStream inputStream = getClass().getResourceAsStream("/application-" + active + ".properties")) {
                properties2.load(inputStream);
            }
            isEnable = Boolean.parseBoolean((String) properties2.get("jwt.enabled"));
            base64Key = (String) properties2.get("jwt.secret.key");
            excludes = properties2.getProperty("jwt.exclude.urls").split(",");
            noPermissionPageUrl = properties2.getProperty("jwt.noPermission.page.url");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("初始化jwtFilter出错!");
        }

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String url = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//        解决web访问的跨越问题
//        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
//        httpResponse.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,headers,token,content-type");
        if (isEnable) {
            String ip = getIpAddr(httpRequest);
            if (isExcludeUri(url)) {
                logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求url:").append(url).append(" 由于是被排除url,通过token").toString());
                filterChain.doFilter(servletRequest, httpResponse);
                return;
            } else {
                String auth = httpRequest.getHeader("token");
                Claims claims = JWTUtil.parseJWT(auth, base64Key);
                if (null != claims) {
                    String username = (String) claims.get("username");
                    String password = (String) claims.get("password");
                    String role = getRoleByUsernamePassword(username, password);
                    if (null != role) {
                        if (role.equals("admin")) {
                            logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求url:").append(url).append(" 由于是admin角色,通过token").toString());
                            filterChain.doFilter(servletRequest, httpResponse);
                            return;
                        } else if (checkUrl(roleVisitedUrl.get(role), url)) {
                            filterChain.doFilter(servletRequest, httpResponse);
                            return;
                        } else {
                            ResultWrapper resultWrapper = new ResultWrapper();
                            resultWrapper.setStatus(ResultStatus.NOT_VALID);
                            resultWrapper.setInfo("未授权的url请求,请与系统管理员联系!");
                            logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求url:").append(url).append(" 未授权的url请求，被拦截").toString());
                            getHttpServletResponse((HttpServletResponse) servletResponse).getWriter().write(JSONObject.toJSONString(resultWrapper));
                            return;
                        }
                    } else {
                        ResultWrapper resultWrapper = new ResultWrapper();
                        resultWrapper.setStatus(ResultStatus.NOT_VALID);
                        resultWrapper.setInfo("找不到该用户的角色，请授权角色权限!");
                        logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求url:").append(url).append(" 找不到该用户的角色，被拦截").toString());
                        getHttpServletResponse((HttpServletResponse) servletResponse).getWriter().write(JSONObject.toJSONString(resultWrapper));
                        return;
                    }
                } else {
                    //过期token或未认证token处理
                    ResultWrapper resultWrapper = new ResultWrapper();
                    resultWrapper.setStatus(ResultStatus.INVALID_TOKEN);
                    logger.info(new StringBuilder("来自ip:").append(ip).append(" 的请求:").append(url).append(" 未知token:[").append(auth).append("] 被拦截！").toString());
                    resultWrapper.setInfo("未知token,请检查");
                    HttpServletResponse httpResponse2 = getHttpServletResponse(httpResponse);
                    httpResponse2.getWriter().write(JSONObject.toJSONString(resultWrapper));
                    return;
                }
            }
        } else {
            filterChain.doFilter(servletRequest, httpResponse);
            return;
        }

    }

    private boolean checkUrl(List<String> list, String url) {
        for (String str : list) {
            if (url.contains(str)) {
                return true;
            }
        }
        return false;
    }

    //获取HttpServletResponse
    private HttpServletResponse getHttpServletResponse(HttpServletResponse servletResponse) {
        HttpServletResponse httpResponse = servletResponse;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return httpResponse;
    }

    @Override
    public void destroy() {
        logger.info("jwtFilter销毁！");
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

    //根据用户名，密码获取角色
    private String getRoleByUsernamePassword(String username, String password) {
        for (Map.Entry<String, List<String>> entry : roleUserNamePassword.entrySet()) {
            if (entry.getValue().contains(username + "_" + password)) {
                return entry.getKey();
            }

        }
        return null;
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
}