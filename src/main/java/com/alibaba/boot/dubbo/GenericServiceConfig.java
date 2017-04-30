package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class GenericServiceConfig implements Serializable {
    private static final long serialVersionUID = 1064223171940612201L;

    //兼容 jsonrpc 如果携带次参数 将以jsonrpc 格式返回
    private String jsonrpc = "2.0";

    //兼容 jsonrpc
    private String id;

    //方法
    @NotNull
    private String method;

    //组名
    private String group;

    //版本
    private String version;

    //参数
    private JsonNode params;

    //参数类型
    private String[] paramsType;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        if (StringUtils.isBlank(group)) {
            return;
        }
        this.group = group;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return;
        }
        this.version = version;
    }



    public GenericServiceConfig() {
    }

    @Override
    public String toString() {
        return "GenericServiceConfig{" +
                ", method='" + method + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                ", params=" + params +
                '}';
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParams(JsonNode params) {
        this.params = params;
    }

    public JsonNode getParams() {
        return params;
    }

    public String[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(String[] paramsType) {
        this.paramsType = paramsType;
    }
}