package com.vale.valechat;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMPP
public class ValechatBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValechatBackendApplication.class, args);
    }

}
