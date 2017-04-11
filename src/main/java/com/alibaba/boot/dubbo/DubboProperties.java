package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.config.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.dubbo")
public class DubboProperties {

    private String            scan;

    @NestedConfigurationProperty
    private ApplicationConfig application;

    @NestedConfigurationProperty
    private RegistryConfig    registry;

    @NestedConfigurationProperty
    private ProtocolConfig    protocol;

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

}
