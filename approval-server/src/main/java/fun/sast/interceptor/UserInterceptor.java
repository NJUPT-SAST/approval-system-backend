package fun.sast.interceptor;

import fun.sast.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<User> userHolder = new ThreadLocal<>();
}
