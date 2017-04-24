package com.alibaba.dubbo;

import com.alibaba.boot.dubbo.EnableDubboProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by wuyu on 2017/4/22.
 */
@SpringBootApplication
@EnableDubboProxy
public class DubboApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboApplication.class, args);
    }
}
