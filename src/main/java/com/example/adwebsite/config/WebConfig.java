package com.example.adwebsite.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dir = appProperties.getUploadDir();          // /tmp/... 或 D:/...
        // 务必保证末尾有 /
        if (!dir.endsWith(File.separator)) dir += File.separator;
        registry.addResourceHandler("/uploads/ads/**")
                .addResourceLocations("file:" + dir);
    }
}