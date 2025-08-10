package fun.sast.config;

import fun.sast.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册UserInterceptor拦截器
        registry.addInterceptor(new UserInterceptor())
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要拦截的请求路径
                .excludePathPatterns("/public/**");
    }
}
