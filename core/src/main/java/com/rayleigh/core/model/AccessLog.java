package com.rayleigh.core.model;

import com.rayleigh.core.util.StringUtil;
import org.slf4j.MDC;

import java.util.Date;

public class AccessLog {
    // 是否执行成功
    protected String success;
    // 追踪ID
    protected String traceId;
    // 执行用户
    protected String user;
    // 用户IP
    protected String ip;
    // 请求路径
    protected String path;
    // 请求方式
    protected String httpType;
    // 如果没有执行成功, 记录异常堆栈
    protected String cause;
    // 如果没有执行成功, 记录请求参数
    protected String parameters;
    // 如果没有执行成功, 记录请求头
    protected String headers;
    // 如果没有执行成功, 记录查询参数
    protected String queryString;
    // 开始执行时间
    protected long start;
    // 结束执行时间
    protected long end;
    // 执行耗时
    protected long cost;

    @Override
    public String toString() {
        return "TraceLog{" +
                "success=" + success +
                ", traceId='" + traceId + '\'' +
                ", user='" + user + '\'' +
                ", ip='" + ip + '\'' +
                ", path='" + path + '\'' +
                ", httpType='" + httpType + '\'' +
                ", cause='" + cause + '\'' +
                ", parameters='" + parameters + '\'' +
                ", headers='" + headers + '\'' +
                ", queryString='" + queryString + '\'' +
                ", start=" + StringUtil.dateToString(new Date(start)) +
                ", end=" + StringUtil.dateToString(new Date(end)) +
                ", cost=" + cost +
                '}';
    }

    public AccessLog() {
        this.traceId = MDC.get("traceId");
        this.user = MDC.get("user");
        this.ip = MDC.get("ip");
        this.path = MDC.get("path");
        this.httpType = MDC.get("method");
    }

    public String getHttpType() {
        return httpType;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getUser() {
        return user;
    }

    public String getIp() {
        return ip;
    }

    public String getPath() {
        return path;
    }

    public long getCost() {
        return end-start;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
