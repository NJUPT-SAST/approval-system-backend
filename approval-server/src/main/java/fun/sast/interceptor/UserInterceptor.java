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

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    private final JwtUtil jwtUtil;

    @Resource private UserMapper userMapper;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("Token");

        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing token");
            return false;
        }

        // 解析 token，提取用户 code
        String userCode;
        try {
            userCode = jwtUtil.resolveJwt(token); // 应该从 token 中提取 code 字段
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return false;
        }

        // 根据 code 字段查询用户
        User user =
                userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getCode, userCode));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: User not found");
            return false;
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
