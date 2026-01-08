package com.example.adwebsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // JAR 同级目录
        String baseDir = System.getProperty("user.dir") + "/uploads/ads/";

        registry.addResourceHandler("/uploads/ads/**")
                .addResourceLocations("file:" + baseDir);
    }
}
