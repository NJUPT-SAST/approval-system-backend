package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** 一个用于测试 JwtUtil 工具类的 JUnit 5 测试类。 它验证了在各种条件下 JWT 的创建和解析功能。 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "test-secret-key-for-jwt-256-bit-long-string";
    private static final long TEST_EXPIRATION = 3600000L; // 1小时，单位为毫秒

    /** 在每个测试方法执行前，设置 JwtUtil 实例。 我们手动创建实例，以便为测试提供密钥和过期时间。 */
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, TEST_EXPIRATION);
    }

    /** 测试用例，用于验证 JWT 是否成功创建并包含正确的声明。 */
    @Test
    void testCreateJwt_Success() {
        String testCode = "user123";
        String token = jwtUtil.createJwt(testCode);
        assertNotNull(token, "生成的令牌不应为空");
        assertFalse(token.isEmpty(), "生成的令牌不应为空字符串");

        // 手动解码令牌，以验证其声明，而不使用 resolveJwt 方法
        Date now = new Date();
        long expirationTimeMs = now.getTime() + TEST_EXPIRATION;
        Date decodedExp = JWT.decode(token).getExpiresAt();

        assertEquals(
                testCode,
                JWT.decode(token).getClaim("code").asString(),
                "'code' 声明应该与输入的 code 相匹配");

        // 我们检查解码后的过期时间是否在预期时间的合理范围内
        assertTrue(
                decodedExp.getTime() > expirationTimeMs - 1000
                        && decodedExp.getTime() <= expirationTimeMs,
                "过期时间应该设置正确");
    }

    /** 测试用例，用于验证有效的令牌是否可以成功解析。 */
    @Test
    void testResolveJwt_Success() {
        String testCode = "testUser456";
        String token = jwtUtil.createJwt(testCode);
        String resolvedCode = jwtUtil.resolveJwt(token);
        assertEquals(testCode, resolvedCode, "解析出的 code 应该与原始 code 匹配");
    }

    /** 测试用例，用于验证过期的令牌是否抛出 TokenExpiredException。 */
    @Test
    void testResolveJwt_ExpiredToken() {
        // 创建一个过期时间在过去的令牌，以确保它已过期
        String testCode = "expiredUser";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // 设置为一秒前
        String expiredToken =
                JWT.create()
                        .withClaim("code", testCode)
                        .withExpiresAt(expiredDate)
                        .sign(Algorithm.HMAC256(TEST_SECRET));

        // 使用 assertThrows 检查预期的异常
        BaseException exception =
                assertThrows(BaseException.class, () -> jwtUtil.resolveJwt(expiredToken));

        // 验证异常是否包含正确的错误枚举
        assertEquals(ErrorEnum.EXPIRED_LOGIN, exception.getErrorEnum(), "过期令牌的异常应该是 EXPIRED_LOGIN");
    }

    /** 测试用例，用于验证无效令牌（被篡改或使用错误的密钥签名）是否抛出通用的 TOKEN_ERROR 异常。 */
    @Test
    void testResolveJwt_InvalidToken() {
        String testCode = "invalidUser";
        String validToken = jwtUtil.createJwt(testCode);

        // 通过改变一个字符来篡改令牌
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "abcde";

        BaseException exception =
                assertThrows(BaseException.class, () -> jwtUtil.resolveJwt(tamperedToken));

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum(), "无效令牌应抛出 TOKEN_ERROR 异常");
    }

    /** 测试用例，用于验证缺少 "code" 声明的令牌是否抛出 TOKEN_ERROR 异常。 */
    @Test
    void testResolveJwt_TokenMissingClaim() {
        // 手动创建一个不含 "code" 声明的 JWT
        String tokenWithoutCode =
                JWT.create()
                        .withClaim("otherClaim", "someValue")
                        .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(TEST_SECRET));

        BaseException exception =
                assertThrows(BaseException.class, () -> jwtUtil.resolveJwt(tokenWithoutCode));

        // 根据实际行为，它会抛出 TOKEN_ERROR 而不是 COMMON_ERROR
        assertEquals(
                ErrorEnum.TOKEN_ERROR,
                exception.getErrorEnum(),
                "缺少 'code' 声明的令牌应抛出 TOKEN_ERROR 异常");
    }
}
