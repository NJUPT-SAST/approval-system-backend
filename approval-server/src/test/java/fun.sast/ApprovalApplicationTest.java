package fun.sast;

import static org.junit.jupiter.api.Assertions.*;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import fun.sast.utils.JwtUtil;
import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    // 测试用
    private final String testSecret = "testSecret123";
    private final long testExpiration = 1000;
    private final JwtUtil jwtUtil = new JwtUtil(testSecret, testExpiration);

    @Test
    void testMissingCodeClaim() {
        // 不带code的token
        String token =
                com.auth0
                        .jwt
                        .JWT
                        .create()
                        .withIssuer("test")
                        .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(testSecret));

        BaseException ex = assertThrows(BaseException.class, () -> jwtUtil.resolveJwt(token));
        assertEquals(ErrorEnum.TOKEN_ERROR, ex.getErrorEnum());
    }

    @Test
    void testExpiredToken() {
        // 过期的token
        String token =
                com.auth0
                        .jwt
                        .JWT
                        .create()
                        .withClaim("code", "user123")
                        .withIssuedAt(new Date(System.currentTimeMillis() - 2000))
                        .withExpiresAt(new Date(System.currentTimeMillis() - 1000)) //
                        .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(testSecret));

        BaseException ex = assertThrows(BaseException.class, () -> jwtUtil.resolveJwt(token));
        assertEquals(ErrorEnum.EXPIRED_LOGIN, ex.getErrorEnum());
    }
}
