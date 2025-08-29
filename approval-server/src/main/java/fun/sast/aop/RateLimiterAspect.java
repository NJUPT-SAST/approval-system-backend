package fun.sast.aop;

import fun.sast.Exception.BaseException;
import fun.sast.annotation.RateLimited;
import fun.sast.enums.ErrorEnum;
import fun.sast.utils.JwtUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    private final JwtUtil jwtUtil;

    public RateLimiterAspect(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 缓存每个接口的桶
    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    // 创建桶
    private Bucket createBucket(RateLimited annotation) {
        Refill refill =
                Refill.intervally(
                        annotation.refillTokens(),
                        Duration.ofSeconds(annotation.refillPeriodSeconds()));
        Bandwidth limit = Bandwidth.classic(annotation.capacity(), refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Around("@annotation(fun.sast.annotation.RateLimited)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RateLimited annotation = method.getAnnotation(RateLimited.class);

        // 获取key
        String key = getKey(annotation, method);

        // 构建or获取桶
        Bucket bucket = bucketCache.computeIfAbsent(key, k -> createBucket(annotation));

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            log.warn("接口{}被限流", key);
            throw new BaseException(ErrorEnum.TOO_MANY_REQUESTS);
        }
    }

    private @NotNull String getKey(RateLimited annotation, Method method) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        // 优先从JWT解析userId
        String userId = jwtUtil.resolveJwt(request.getHeader("Token"));

        // 如果userId为空，则回退到ip
        if (userId == null || userId.isEmpty()) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                userId = ip.split(",")[0]; // 多级代理时取第一个
            } else {
                ip = request.getHeader("Proxy-Client-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    userId = ip;
                } else {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                        userId = ip;
                    } else {
                        userId = request.getRemoteAddr();
                    }
                }
            }
        }

        // 没指定key默认类名#方法名
        String key = annotation.key();
        if (key.isEmpty()) {
            key = method.getDeclaringClass().getName() + "#" + method.getName();
        }
        return key + ":" + userId;
    }
}
