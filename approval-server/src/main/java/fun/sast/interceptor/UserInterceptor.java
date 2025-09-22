package fun.sast.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.User;
import fun.sast.mapper.UserMapper;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
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
    private final RedisUtil redisUtil;

    @Resource private final UserMapper userMapper;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("Token");
        String requestPath = request.getRequestURI();

        // 对于某些端点，允许未登录访问但仍尝试解析身份
        boolean isOptionalAuth = requestPath.equals("/com/notice/list");

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

        // 先查 Redis 缓存
        String redisKey = "user:" + userCode;
        User user = (User) redisUtil.get(redisKey, User.class);

        // 没有就查数据库
        if (user == null) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getCode, userCode));

            if (user == null) {
                if (isOptionalAuth) {
                    return true;
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized: Invalid token");
                    return false;
                }
            }
            // 放Redis60分钟过期
            redisUtil.set(redisKey, user, 60 * 60);
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
