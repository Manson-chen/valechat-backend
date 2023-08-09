package com.vale.valechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
     * 异步请求配置
     *
     * @param configurer
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    /**
     * 配置拦截器、拦截路径
     * 每次请求到拦截的路径，就会去执行拦截器中的方法
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        excludePath.add("/**");
//        excludePath.add("/users/login");  //登录
//        excludePath.add("/users/register");     //注册
//        excludePath.add("/users/activate/**");
//        excludePath.add("/users/password/forget");
//        excludePath.add("/pm/password/forget");
//        excludePath.add("/pm/login");
//        excludePath.add("/pm/register");
//        excludePath.add("/pm/activate/**");
//        excludePath.add("/topics/**");
//        excludePath.add("/comments/**");
//        excludePath.add("/doc.html");     //swagger
//        excludePath.add("/swagger-ui.html");     //swagger
//        excludePath.add("/swagger-resources/**");     //swagger
//        excludePath.add("/v2/api-docs");     //swagger
//        excludePath.add("/webjars/**");     //swagger
//        excludePath.add("/static/**");  //静态资源
//        excludePath.add("/assets/**");  //静态资源
        registry.addInterceptor(tokenInterceptor)
//                .addPathPatterns("/**")
                .addPathPatterns("/user/update");
//                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}