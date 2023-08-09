package com.vale.valechat.oauth;


import com.vale.valechat.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class  WebSecurityConfig{

    @Resource
    private CustomOAuth2UserService oAuth2UserService;

    @Resource
    private UserService userService;

    @Resource
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()   //Disable CSRF token //从 Spring Security4 开始 CSRF 防护默认开启。默认会拦截请求。进行 CSRF 处理。CSRF 为了保证不是其他第三方网站访问，要求访问时携带参数名为_csrf 值为 token(token 在服务端产生)的内容，如果token 和服务端的 token 匹配成功，则正常访问。
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/oauth/login", "/oauth2/**").permitAll()
                .antMatchers("/**").permitAll()
                // Indicates that all other requests require authentication
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                    .userInfoEndpoint().userService(oAuth2UserService)
                    .and()
                    .successHandler(oAuth2LoginSuccessHandler)
                .and()
                .build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()   //Disable CSRF token
//                .cors()
//                .and()
//                .authorizeRequests()
//                .antMatchers("/user/login").permitAll()
//                .antMatchers("/user/oauth/login", "/oauth2/**").permitAll()
//                .antMatchers("/**").permitAll()
//                // Indicates that all other requests require authentication
//                .anyRequest().authenticated()
//                .and()
//                .oauth2Login()
//                    .userInfoEndpoint().userService(oAuth2UserService)
//                    .and()
//                    .successHandler(oAuth2LoginSuccessHandler);
//    }
}
