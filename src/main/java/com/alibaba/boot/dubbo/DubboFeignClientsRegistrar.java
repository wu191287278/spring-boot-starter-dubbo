package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by wuyu on 2017/4/18.
 */
public class DubboFeignClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerFeignClients(metadata, registry);
    }


    public void registerFeignClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String scan = resolve("${spring.dubbo.scan}");
        if (StringUtils.isEmpty(scan)) {
            return;
        }
        if (ClassUtils.isPresent("org.springframework.cloud.netflix.feign.FeignClient", this.getClass().getClassLoader())) {
            ClassPathScanningCandidateComponentProvider provider = getScanner();
            provider.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
            for (String basePackage : scan.split(",")) {
                Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(basePackage);
                for (BeanDefinition candidateComponent : candidateComponents) {

                    AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ReferenceBean.class)
                            .addPropertyValue("interface", candidateComponent.getBeanClassName())
                            .getBeanDefinition();
                    beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                    registry.registerBeanDefinition(getAliasName(candidateComponent.getBeanClassName()), beanDefinition);
                }
            }
        }
    }

    protected String getAliasName(String className) {
        try {
            Class<?> client = ClassUtils.forName(className, DubboFeignClientsRegistrar.class.getClassLoader());
            FeignClient feignClient = client.getAnnotation(FeignClient.class);
            if (!StringUtils.isEmpty(feignClient.qualifier())) {
                return feignClient.qualifier();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String name = className.substring(className.lastIndexOf(".") + 1);
        if (className.length() > 1) {
            return name.substring(0, 1).toLowerCase() + name.substring(1) + "DubboClient";
        }
        return className;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    // TODO until SPR-11711 will be resolved
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    DubboConfigurationApplicationContextInitializer.class.getClassLoader());
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: "
                                            + beanDefinition.getMetadata().getClassName(),
                                    ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)
                && this.resourceLoader instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment()
                    .resolvePlaceholders(value);
        }
        return value;
    }
}

