package com.alibaba.boot.dubbo.endpoint;

import java.util.List;

public class ProviderBean {

    private String       target;
    private String       interfaceName;
    private String       version;
    private String       group;
    private Integer      timeout;
    private List methodNames;

    private Integer connections;
    private String protocol;
    private Integer retries;
    private Integer actives;
    private String loadbalance;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


    public List getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(List methodNames) {
        this.methodNames = methodNames;
    }

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
