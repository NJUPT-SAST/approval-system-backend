package fun.sast.utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OSSRateLimiterUtil {

    private static final int DEFAULT_LIMIT = 5; // 默认最大请求数
    private static final int DEFAULT_WINDOW_SECONDS = 60; // 默认窗口大小（秒）

    @Autowired RedisUtil redisUtil;

    /** 默认：5 次 / 60 秒 */
    public boolean tryAcquire(String key) {
        return tryAcquire(
                key, DEFAULT_LIMIT, DEFAULT_WINDOW_SECONDS * 1000L, DEFAULT_WINDOW_SECONDS * 2L);
    }

    /**
     * 自定义：limit 次 / windowLength 毫秒
     *
     * @param key 用户标识（建议在外层加 download: upload: 前缀）
     * @param limit 窗口内允许的最大请求数
     * @param windowLength 窗口长度（毫秒）
     * @param ttlSeconds Redis key 过期时间（秒）
     * @return true 允许访问，false 超过限制
     */
    public boolean tryAcquire(String key, int limit, long windowLength, long ttlSeconds) {
        long now = Instant.now().toEpochMilli(); // 毫秒
        String redisKey = "oss:rate:" + key;

        Object existing = redisUtil.get(redisKey);
        List<Long> timestamps;
        // 滑动窗口限流
        if (existing != null) {
            timestamps =
                    Stream.of(existing.toString().split(","))
                            .map(Long::parseLong)
                            .filter(ts -> ts > now - windowLength)
                            .collect(Collectors.toList());
        } else {
            timestamps = new ArrayList<>();
        }

        // 超过限制
        if (timestamps.size() >= limit) {
            return false;
        }

        // 加入当前请求时间
        timestamps.add(now);
        String value = timestamps.stream().map(String::valueOf).collect(Collectors.joining(","));
        redisUtil.set(redisKey, value, ttlSeconds);
        return true;
    }
}
