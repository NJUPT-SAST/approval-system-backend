package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/** RedisUtil 的单元测试类。 使用 Mockito 模拟 RedisTemplate，以在不连接实际 Redis 实例的情况下进行测试。 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisUtilTest {

    // 模拟 RedisTemplate
    @Mock private RedisTemplate<String, Object> redisTemplate;

    // 模拟 ValueOperations，用于处理 String 类型的操作
    @Mock private ValueOperations<String, Object> valueOperations;

    // 注入被测试对象，Mock 的 redisTemplate 将被注入到其中
    @InjectMocks private RedisUtil redisUtil;

    /** 在每个测试方法执行前进行初始化设置。 这里我们模拟 redisTemplate.opsForValue() 方法，使其返回我们模拟的 valueOperations。 */
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /** 测试设置缓存（无时间限制） */
    @Test
    void testSet_withoutTimeout() {
        String key = "key1";
        String value = "value1";

        redisUtil.set(key, value);

        // 验证 valueOperations.set 方法是否被调用，且参数正确
        verify(valueOperations, times(1)).set(key, value);
    }

    /** 测试设置缓存（有时间限制） */
    @Test
    void testSet_withTimeout() {
        String key = "key2";
        String value = "value2";
        long timeout = 60L;

        redisUtil.set(key, value, timeout);

        // 验证 valueOperations.set 方法是否被调用，且参数正确，包括超时时间和单位
        verify(valueOperations, times(1)).set(key, value, timeout, TimeUnit.SECONDS);
    }

    /** 测试删除缓存并成功的情况 */
    @Test
    void testDelete_success() {
        String key = "key3";
        // 模拟 redisTemplate.delete 返回 true，表示成功删除一个键
        when(redisTemplate.delete(key)).thenReturn(true);
        // 为了兼容您原始的代码，我们将 hasKey 模拟为 false
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean result = redisUtil.delete(key);

        // 验证 redisTemplate.delete 方法被调用
        verify(redisTemplate, times(1)).delete(key);
        // 断言返回结果为 true
        assertTrue(result);
    }

    /** 测试删除缓存但失败的情况 */
    @Test
    void testDelete_failure() {
        String key = "key4";
        // 模拟 redisTemplate.delete 返回 false，表示没有键被删除
        when(redisTemplate.delete(key)).thenReturn(false);
        // 为了兼容您原始的代码，我们将 hasKey 模拟为 true
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = redisUtil.delete(key);

        // 验证 redisTemplate.delete 方法被调用
        verify(redisTemplate, times(1)).delete(key);
        // 断言返回结果为 false
        assertFalse(result);
    }

    /** 测试获取现有缓存 */
    @Test
    void testGet_existingKey() {
        String key = "key5";
        String expectedValue = "value5";
        // 模拟 key 存在
        when(redisTemplate.hasKey(key)).thenReturn(true);
        // 模拟获取值操作返回预期的值
        when(valueOperations.get(key)).thenReturn(expectedValue);

        Object result = redisUtil.get(key);

        // 断言返回结果与预期值相等
        assertEquals(expectedValue, result);
    }

    /** 测试获取不存在的缓存 */
    @Test
    void testGet_nonExistingKey() {
        String key = "key6";
        // 模拟 key 不存在
        when(redisTemplate.hasKey(key)).thenReturn(false);

        Object result = redisUtil.get(key);

        // 断言返回结果为 null
        assertNull(result);
    }

    /** 测试获取有过期时间的缓存 */
    @Test
    void testGetExpire_withTimeout() {
        String key = "key7";
        long expectedExpire = 300L;
        // 模拟 key 存在
        when(redisTemplate.hasKey(key)).thenReturn(true);
        // 模拟获取过期时间
        when(redisTemplate.getExpire(key)).thenReturn(expectedExpire);

        long result = redisUtil.getExpire(key);

        // 断言返回结果与预期过期时间相等
        assertEquals(expectedExpire, result);
    }

    /** 测试获取无过期时间的缓存 */
    @Test
    void testGetExpire_withoutTimeout() {
        String key = "key8";
        // 模拟 key 存在
        when(redisTemplate.hasKey(key)).thenReturn(true);
        // 模拟获取过期时间，返回 -1L
        when(redisTemplate.getExpire(key)).thenReturn(-1L);

        long result = redisUtil.getExpire(key);

        // 断言返回结果为 -1L
        assertEquals(-1L, result);
    }

    /** 测试获取不存在的缓存的过期时间 */
    @Test
    void testGetExpire_nonExistingKey() {
        String key = "key9";
        // 模拟 key 不存在
        when(redisTemplate.hasKey(key)).thenReturn(false);

        long result = redisUtil.getExpire(key);

        // 断言返回结果为 -2L
        assertEquals(-2L, result);
    }
}
