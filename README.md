#spring-boot-start-dubbo

* Dubbo是阿里开发的一套分布式通讯框架,Spring-boot是业界比较火的微服务框架，两者可以进行结合实现分布式微服务
* 对于内部远程Rpc调用，可以借用Dubbo能力，达到服务治理的目的

##增加feign protocol支持。

> 该协议主要是为了支持老项目可以消费springcloud提供的接口，并可以利用dubbo的服务发现，构建出一个springboot rest集群，
> dubbo与springboot结合时，不需要dubbo再次导出rest服务。而是由springboot提供rest服务dubbo端只负责注册，构建服务目录。


添加以下maven

```
<!--Feign 支持-->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
</dependency>
```
###feign示例

```
@Bean
//服务端，多协议发布服务
public ServiceBean<UserService> userServiceServiceBean(@Autowired UserService userService) {
    ServiceBean<UserService> serviceBean = new ServiceBean<UserService>();
    serviceBean.setInterface(UserService.class);
    serviceBean.setRef(userService);
    serviceBean.setProtocols(Arrays.asList(new ProtocolConfig("dubbo"), new ProtocolConfig("feign", port)));
    return serviceBean;
}

@Bean
//消费端，此种方式可以避免使用@Reference注解，保持与spring注解一致
public ReferenceBean<UserService> userService() {
    ReferenceBean<UserService> bean = new ReferenceBean<UserService>();
    bean.setInterface(UserService.class);
    return bean;
}


@FeignClient(path = "/user")
public interface UserService {

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    User findOne(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void save(@RequestBody User user);

    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody User user);

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    List<User> findAll();

}

```


##如何发布Dubbo服务
在Spring Boot项目的pom.xml中添加以下依赖:

```

 <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>spring-boot-starter-dubbo</artifactId>
        <version>1.4.5.SNAPSHOT</version>
 </dependency>
 
 <!--依赖于容器-->

 <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
         <version>1.4.5.RELEASE</version>
 </dependency>

 ```

在application.properties添加Dubbo的版本信息和客户端超时信息,如下:

```
#dubbo produce

spring.dubbo.application.name=comment-provider
spring.dubbo.registry.protocol=zookeeper
spring.dubbo.registry.address=monkey:2181,127.0.0.1:2181
spring.dubbo.protocol.name=dubbo
spring.dubbo.protocol.port=20880
spring.dubbo.scan=com.vcg.comment.service
spring.dubbo.protocol.host=发布的hostname


在Spring Application的application.properties中添加spring.dubbo.scan即可支持Dubbo服务发布,其中scan表示要扫描的package目录
```
spring boot启动
```

@SpringBootApplication
@EnableDubboAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
编写你的Dubbo服务,只需要添加要发布的服务实现上添加 @Service ,如下:

```
@Service(version = "1.0.0")
public class CommentServiceImpl implements CommentService {

    @Override
    public String test() {
        return "hello";
    }
}

如果你不喜欢Dubbo的@Service注解,而是喜欢原生的Spring @Service注解,可以采用以下方式对外发布服务
@Configurable
public class BeanConfiguration {

    MonitorConfig monitorConfig;

    @Autowire
    public void setMonitorConfig(MonitorConfig monitorConfig){
        this.monitorConfig=monitorConfig;
    }

    @Bean
    public ServiceBean<CommentService> commentServiceServiceBean(CommentService commentService) {
        ServiceBean<CommentService> serviceBean = new ServiceBean<>();
        serviceBean.setInterface(CommentService.class);
        //开启监控
        serviceBean.setMonitor(monitorConfig);
        serviceBean.setRef(commentService);
        return serviceBean;
    }
}
```

在application.properties添加Dubbo的版本信息和客户端超时信息,如下:

#dubbo consumer
```
spring.dubbo.application.name=comment-consumer
spring.dubbo.registry.protocol=zookeeper
spring.dubbo.registry.address=monkey:2181
spring.dubbo.scan=com.vcg
在Spring Application的application.properties中添加spring.dubbo.scan即可支持Dubbo服务发布,其中scan表示要扫描的package目录

```

引用Dubbo服务,只需要添加要发布的服务实现上添加 @Reference ,如下:

```

@Component
public class UserController {

    @Reference(version = "1.0.0")
    private CommentService commentService;
}

```

如果你不喜欢@Reference注入服务,而是用@Autowired可以采用以下方式.

```
@Configurable
public class BeanConfiguration {


    MonitorConfig monitorConfig;

    @Autowire
    public void setMonitorConfig(MonitorConfig monitorConfig){
        this.monitorConfig=monitorConfig;
    }

    @Bean
    public ReferenceBean<CommentService> commentService(){
        ReferenceBean<CommentService> commentServiceBean=new ReferenceBean<>();
        commentServiceBean.setInterface(CommentService.class);
        commentServiceBean.setMonitor(monitorConfig);
        return commentServiceBean;
    }
}

```

引用Dubbo服务,引用以上服务:

```
@Component
public class UserController {

    @Autowired
    private CommentService commentService;

}
```