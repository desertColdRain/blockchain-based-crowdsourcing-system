package com.blockchain.mcsblockchain.conf;

import com.blockchain.mcsblockchain.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
       registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                //放行路径，可以添加多个
                .excludePathPatterns("/login").excludePathPatterns("/register");
    }
}
