package com.vale.valechat.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Set time zone to local time zone
            builder.timeZone(TimeZone.getDefault());
            // Set date formatting rules
            builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        };
    }
}