package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.lang.reflect.Field;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class JwtUtilTest {

    @InjectMocks private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // 使用反射设置私有字段的值
        setField(jwtUtil, "secret", "testSecret");
        setField(jwtUtil, "expiration", 86400000L); // 24小时
    }

    private void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
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
    void testResolveJwt_withExpiredToken() throws NoSuchFieldException, IllegalAccessException {
        // Given
        // 设置一个已过期的时间
        setField(jwtUtil, "expiration", -1000L); // 设置为过去的时间，确保token已过期
        String code = "testUserCode";
        String token = jwtUtil.createJwt(code);

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            jwtUtil.resolveJwt(token);
                        });

        assertEquals(ErrorEnum.EXPIRED_LOGIN, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withInvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            jwtUtil.resolveJwt(invalidToken);
                        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withNullToken() {
        // Given
        String nullToken = null;

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            jwtUtil.resolveJwt(nullToken);
                        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withEmptyToken() {
        // Given
        String emptyToken = "";

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            jwtUtil.resolveJwt(emptyToken);
                        });

        assertEquals(ErrorEnum.TOKEN_ERROR, exception.getErrorEnum());
    }

    @Test
    void testResolveJwt_withTokenHavingEmptyCode()
            throws NoSuchFieldException, IllegalAccessException {
        // Given
        // 获取JwtUtil实例中的secret值
        Field secretField = jwtUtil.getClass().getDeclaredField("secret");
        secretField.setAccessible(true);
        String secret = (String) secretField.get(jwtUtil);

        // 获取过期时间
        Field expirationField = jwtUtil.getClass().getDeclaredField("expiration");
        expirationField.setAccessible(true);
        long expiration = (Long) expirationField.get(jwtUtil);

        // 使用与JwtUtil中完全相同的方式创建token，但不包含code声明
        Date now = new Date();
        Date expDate = new Date(now.getTime() + expiration);

        String tokenWithoutCode =
                JWT.create()
                        .withIssuedAt(now)
                        .withExpiresAt(expDate)
                        .sign(Algorithm.HMAC256(secret));

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            jwtUtil.resolveJwt(tokenWithoutCode);
                        });

        // Token有效但不包含code声明，应抛出COMMON_ERROR
        assertEquals(ErrorEnum.COMMON_ERROR, exception.getErrorEnum());
    }

    @Test
    void testCreateJwt_generatesValidToken() {
        // Given
        String code = "testUserCode";

        // When
        String token = jwtUtil.createJwt(code);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }
}
