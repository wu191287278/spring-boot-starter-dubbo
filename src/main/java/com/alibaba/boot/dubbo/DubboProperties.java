package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.config.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.dubbo")
public class DubboProperties {

    private String            scan = "";

    //支持扫描@RestController注解
    private boolean           restController = true;

    //全局超时时间
    private Integer           timeout = 1000;

    @NestedConfigurationProperty
    private ApplicationConfig application;

    @NestedConfigurationProperty
    private RegistryConfig    registry;

    @NestedConfigurationProperty
    private ProtocolConfig    protocol = new ProtocolConfig("dubbo",20880);

    public String getScan() {
        return scan;
    }

    public ApplicationConfig getApplication() {
        return application;
    }

    public void setApplication(ApplicationConfig application) {
        this.application = application;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public ProtocolConfig getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocol = protocol;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }


    public boolean isRestController() {
        return restController;
    }

    public void setRestController(boolean restController) {
        this.restController = restController;
    }


    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
