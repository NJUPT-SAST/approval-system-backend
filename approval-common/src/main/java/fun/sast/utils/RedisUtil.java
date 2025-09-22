package fun.sast.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    @Resource private RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * 设置缓存（没有时间限制）
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（有时间限制，单位为秒）
     *
     * @param key
     * @param value
     * @param timeout
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存，并返回是否删除成功
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        redisTemplate.delete(key);
        // 如果还存在这个 key 就证明删除失败
        if (redisTemplate.hasKey(key)) {
            return false;
            // 不存在就证明删除成功
        } else {
            return true;
        }
    }

    /**
     * 取出缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().get(key);
        } else {
            return null;
        }
    }

    /**
     * 获取失效时间（-2：失效 / -1：没有时间限制）
     *
     * @param key
     * @return
     */
    public long getExpire(String key) {
        // 判断是否存在
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.getExpire(key);
        } else {
            return -2L;
        }
    }

    /** 泛型获取对象，并自动转 User / 自定义类型 */
    public <T> T get(String key, Class<T> clazz) {
        Object obj = get(key);
        if (obj == null) return null;
        // 使用 ObjectMapper 转换
        return objectMapper.convertValue(obj, clazz);
    }
}
