package fun.sast.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // 使用反射设置私有字段的值
        setField(jwtUtil, "secret", "testSecret");
        setField(jwtUtil, "expiration", 86400000L); // 24小时
    }

    private void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testCreateJwt_and_ResolveJwt_withValidCode() {
        // Given
        String code = "testUserCode";

        // When
        String token = jwtUtil.createJwt(code);
        String resolvedCode = jwtUtil.resolveJwt(token);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(code, resolvedCode);
    }

    @Test
    void testGenerateToken_and_ResolveJwt_withValidCode() {
        // Given
        String code = "testUserCode";

        // When
        String token = jwtUtil.generateToken(code);
        String resolvedCode = jwtUtil.resolveJwt(token);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(code, resolvedCode);
    }

    @Test
    void testResolveJwt_withExpiredToken() throws NoSuchFieldException, IllegalAccessException {
        // Given
        // 设置一个很短的过期时间来创建一个即将过期的token
        setField(jwtUtil, "expiration", 1L); // 1毫秒
        String code = "testUserCode";
        String token = jwtUtil.createJwt(code);

        // 等待token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            jwtUtil.resolveJwt(token);
        });

        assertEquals(ErrorEnum.EXPIRED_LOGIN, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withInvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            jwtUtil.resolveJwt(invalidToken);
        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withNullToken() {
        // Given
        String nullToken = null;

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            jwtUtil.resolveJwt(nullToken);
        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withEmptyToken() {
        // Given
        String emptyToken = "";

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            jwtUtil.resolveJwt(emptyToken);
        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withTokenHavingEmptyCode() throws NoSuchFieldException, IllegalAccessException {
        // Given
        // 创建一个没有code字段的token
        String secret = "testSecret";
        String tokenWithoutCode = JWT.create()
                .withClaim("other", "value")
                .sign(Algorithm.HMAC256(secret));

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            jwtUtil.resolveJwt(tokenWithoutCode);
        });

        assertEquals(ErrorEnum.COMMON_ERROR, exception.getErrorEnum());
    }
}
