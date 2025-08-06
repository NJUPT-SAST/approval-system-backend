package fun.sast.interceptor;

import fun.sast.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInterceptor implements HandlerInterceptor {

    // 使用ThreadLocal存储用户信息，保证线程安全
    public static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    // 设置当前线程的用户信息
    public static void setUser(User user) {
        userHolder.set(user);
    }

    // 获取当前线程的用户信息
    public static User getUser() {
        return userHolder.get();
    }

    // 清除当前线程的用户信息，防止内存泄漏
    public static void removeUser() {
        userHolder.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这里应该从请求中获取用户信息，例如从token中解析
        // 为了演示，这里暂时设置一个空用户
        User user = new User();
        setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除用户信息，防止内存泄漏
        removeUser();
    }

}


    

