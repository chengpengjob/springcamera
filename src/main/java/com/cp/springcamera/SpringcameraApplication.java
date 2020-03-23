package com.cp.springcamera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.cp.springcamera.mapper")
public class SpringcameraApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcameraApplication.class, args);
    }

}
