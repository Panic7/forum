package com.example.forum.configuration;

import com.example.forum.common.StringConstants;
import com.example.forum.security.jwt.LoginUserInterceptor;
import com.example.forum.security.jwt.LoginUserResolver;
import com.example.forum.service.CategoryInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {


    private final LoginUserResolver loginUserResolver;
    private final LoginUserInterceptor loginUserInterceptor;
    private final CategoryInterceptor categoryInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor)
                .addPathPatterns("", "/**")
                .excludePathPatterns(StringConstants.SKIP_URLS);
        registry.addInterceptor(categoryInterceptor)
                .addPathPatterns("", "/**")
                .excludePathPatterns(StringConstants.SKIP_URLS);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserResolver);
    }
}
