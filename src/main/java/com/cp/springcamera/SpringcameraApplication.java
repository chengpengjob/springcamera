package com.cp.springcamera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan(basePackages = "com.cp.springcamera.mapper")
public class SpringcameraApplication{

    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(SpringcameraApplication.class);

    }*/

    public static void main(String[] args) {
        SpringApplication.run(SpringcameraApplication.class, args);
    }

}
