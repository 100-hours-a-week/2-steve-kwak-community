package com.example.demo.config;

import com.example.demo.filter.Utf8CharacterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Utf8CharacterFilter> loggingFilter() {
        FilterRegistrationBean<Utf8CharacterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new Utf8CharacterFilter());
        registrationBean.addUrlPatterns("/posts/*");  // 필터를 적용할 URL 패턴
        return registrationBean;
    }
}
