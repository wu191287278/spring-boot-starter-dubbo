package com.alibaba.boot.dubbo;

import com.alibaba.boot.dubbo.endpoint.DubboEndpoint;
import com.alibaba.boot.dubbo.health.DubboHealthIndicator;
import com.alibaba.boot.dubbo.zipkin.ConsumerSpan;
import com.alibaba.boot.dubbo.zipkin.DubboSpanExtractor;
import com.alibaba.boot.dubbo.zipkin.DubboSpanInjector;
import com.alibaba.boot.dubbo.zipkin.ProviderSpan;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(DubboProperties.class)
public class DubboAutoConfiguration {

    @Autowired
    private DubboProperties dubboProperties;

    @Bean
    @ConditionalOnMissingBean
    public ApplicationConfig applicationConfig() {
        return dubboProperties.getApplication();
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfig registryConfig() {
        return dubboProperties.getRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtocolConfig protocolConfig() {
        return dubboProperties.getProtocol();
    }

    @Bean
    @ConfigurationProperties(prefix = "endpoints.dubbo", ignoreUnknownFields = false)
    public DubboEndpoint dubboEndpoint() {
        return new DubboEndpoint();
    }

    @Bean
    public DubboHealthIndicator dubboHealthIndicator() {
        return new DubboHealthIndicator();
    }


//    @Bean
//    @ConditionalOnClass(Tracer.class)
//    public DubboSpanInjector dubboSpanInjector() {
//        return new DubboSpanInjector();
//    }
//
//    @Bean
//    @ConditionalOnClass(Tracer.class)
//    public DubboSpanExtractor dubboSpanExtractor() {
//        return new DubboSpanExtractor();
//    }
//
//    @Bean
//    @ConditionalOnBean(value = {DubboSpanInjector.class,DubboSpanExtractor.class})
//    public ProviderSpan providerSpan() {
//        return new ProviderSpan();
//    }
//
//    @Bean
//    @ConditionalOnBean(value = {DubboSpanInjector.class,DubboSpanExtractor.class})
//    public ConsumerSpan consumerSpan() {
//        return new ConsumerSpan();
//    }
}
