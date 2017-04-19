package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.rpc.Protocol;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wuyu on 2017/4/19.
 */
public class AnnotationBeanConfiguration extends AnnotationBean {

    private ApplicationContext applicationContext;

    private String annotationPackage;

    private ExtensionLoader<Protocol> protocolExtensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);


    /**
     * 去除Dubbo扫描com.alibaba.dubbo.config.annotation.Service，仅把Service做为描述接口注解使用
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    public void setPackage(String annotationPackage) {
        super.setPackage(annotationPackage);
        this.annotationPackage = annotationPackage;
    }

    /**
     * 修复 @Service 注解bug
     *
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (!isMatchPackage(bean)) {
            return bean;
        }
        Service service = bean.getClass().getAnnotation(Service.class);
        if (service != null) {
            ServiceBean<Object> serviceConfig = new ServiceBean<Object>(service);
            if (void.class.equals(service.interfaceClass()) && "".equals(service.interfaceName())) {
                if (bean.getClass().getInterfaces().length > 0) {
                    serviceConfig.setInterface(bean.getClass().getInterfaces()[0]);
                } else {
                    throw new IllegalStateException("Failed to export remote service class " + bean.getClass().getName() + ", cause: The @Service undefined interfaceClass or interfaceName, and the service class unimplemented any interfaces.");
                }
            }

            if (applicationContext != null) {
                serviceConfig.setApplicationContext(applicationContext);
                if (service.registry().length > 0) {
                    List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                    for (String registryId : service.registry()) {
                        if (registryId != null && registryId.length() > 0) {
                            registryConfigs.add((RegistryConfig) applicationContext.getBean(registryId, RegistryConfig.class));
                        }
                    }
                    serviceConfig.setRegistries(registryConfigs);
                }
                if (service.provider().length() > 0) {
                    serviceConfig.setProvider(applicationContext.getBean(service.provider(), ProviderConfig.class));
                }
                if (service.monitor().length() > 0) {
                    serviceConfig.setMonitor(applicationContext.getBean(service.monitor(), MonitorConfig.class));
                }
                if (service.application().length() > 0) {
                    serviceConfig.setApplication(applicationContext.getBean(service.application(), ApplicationConfig.class));
                }
                if (service.module().length() > 0) {
                    serviceConfig.setModule(applicationContext.getBean(service.module(), ModuleConfig.class));
                }
                if (service.provider().length() > 0) {
                    serviceConfig.setProvider(applicationContext.getBean(service.provider(), ProviderConfig.class));
                }

                if (service.protocol().length > 0) {
                    List<ProtocolConfig> protocolConfigs = new ArrayList<>();
                    Set<String> supportedExtensions = protocolExtensionLoader.getSupportedExtensions();
                    for (String protocol : service.protocol()) {
                        if (supportedExtensions.contains(protocol)) {
                            protocolConfigs.add(new ProtocolConfig(protocol));
                        } else {
                            protocolConfigs.add(applicationContext.getBean(protocol, ProtocolConfig.class));
                        }
                    }
                    serviceConfig.setProtocols(protocolConfigs);
                }
                try {
                    serviceConfig.afterPropertiesSet();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
            serviceConfig.setRef(bean);


            try {
                Field serviceConfigsField = ReflectionUtils.findField(AnnotationBean.class, "serviceConfigs");
                serviceConfigsField.setAccessible(true);
                Set<ServiceConfig<?>> serviceConfigs = (Set<ServiceConfig<?>>) serviceConfigsField.get(this);
                serviceConfigs.add(serviceConfig);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            serviceConfig.export();
        }
        return bean;
    }

    private boolean isMatchPackage(Object bean) {
        if (annotationPackage == null || annotationPackage.length() == 0) {
            return false;
        }

        String beanClassName = bean.getClass().getName();
        for (String pkg : annotationPackage.split(",")) {
            if (beanClassName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }
}
