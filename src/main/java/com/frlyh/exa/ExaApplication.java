package com.frlyh.exa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync//任务异步
@SpringBootApplication
public class ExaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExaApplication.class, args);
    }

}
