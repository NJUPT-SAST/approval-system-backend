package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OSSRateLimiterUtilTest {

    private OSSRateLimiterUtil rateLimiter;
    private RedisUtil redisUtil;
    private Map<String, String> redisMock;

    @BeforeEach
    void setup() {
        // 初始化模拟 Redis 存储
        redisMock = new HashMap<>();

        // mock RedisUtil
        redisUtil = Mockito.mock(RedisUtil.class);

        // 模拟 get 方法
        Mockito.when(redisUtil.get(Mockito.anyString()))
                .thenAnswer(
                        invocation -> {
                            String key = invocation.getArgument(0);
                            return redisMock.get(key);
                        });

        // 模拟 set 方法
        Mockito.doAnswer(
                        invocation -> {
                            String key = invocation.getArgument(0);
                            String value = invocation.getArgument(1);
                            Long ttl = invocation.getArgument(2); // ttl 秒，这里暂不使用
                            redisMock.put(key, value);
                            return null;
                        })
                .when(redisUtil)
                .set(Mockito.anyString(), Mockito.any(), Mockito.anyLong());

        // 初始化限流工具类
        rateLimiter = new OSSRateLimiterUtil();
        rateLimiter.redisUtil = redisUtil; // 注入 mock RedisUtil
    }

    @Test
    void testGlobalRateLimit() {
        String userKey = "user1";

        // 默认限制 5 次 / 60 秒
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryAcquire("download:" + userKey), "请求 " + (i + 1) + " 应该允许");
        }

        // 第6次请求应被拒绝
        assertFalse(rateLimiter.tryAcquire("download:" + userKey), "第6次请求应该被限流");
    }

    @Test
    void testSingleFileRateLimit() {
        String userKey = "user1";
        String fileKey1 = "download:" + userKey + ":file1";
        String fileKey2 = "download:" + userKey + ":file2";

        // file1 限制 3 次
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimiter.tryAcquire(fileKey1, 3, 60, 120));
        }
        assertFalse(rateLimiter.tryAcquire(fileKey1, 3, 60, 120));

        // file2 独立限制，也允许访问
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimiter.tryAcquire(fileKey2, 3, 60, 120));
        }
        assertFalse(rateLimiter.tryAcquire(fileKey2, 3, 60, 120));
    }
}
