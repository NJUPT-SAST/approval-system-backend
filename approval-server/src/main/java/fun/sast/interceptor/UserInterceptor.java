package fun.sast.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.User;
import fun.sast.mapper.UserMapper;
import fun.sast.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    private final JwtUtil jwtUtil;

    @Resource private UserMapper userMapper;
    
    // 定义不需要token验证的接口路径集合
    private static final Set<String> OPTIONAL_AUTH_PATHS = new HashSet<>(Arrays.asList(
            "/user/com/notice/list",
            "/user/com/search",
            "/user/com/list"
    ));

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("Token");
        String requestPath = request.getRequestURI();

        // 使用集合的contains方法判断当前路径是否不需要token验证
        boolean isOptionalAuth = OPTIONAL_AUTH_PATHS.contains(requestPath);

        if (token == null || token.isBlank()) {
            if (isOptionalAuth) {
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Missing token");
                return false;
            }
        }

        // 解析 token，提取用户 code
        String userCode;
        try {
            userCode = jwtUtil.resolveJwt(token); // 应该从 token 中提取 code 字段
        } catch (Exception e) {
            if (isOptionalAuth) {
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid token");
                return false;
            }
        }

        // 根据 code 字段查询用户
        User user =
                userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getCode, userCode));
        if (user == null) {
            if (isOptionalAuth) {
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: User not found");
                return false;
            }
        }

        // 保存用户到 ThreadLocal
        userHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        userHolder.remove();
    }
}
