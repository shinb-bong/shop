package com.shop.common.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LayOutConfig {
    // thymeleaf layout Config 추가
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
