package fun.sast.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * @param code 用户账号
     * @return 含有code的token
     */
    public String createJwt(String code) {
        Date now = new Date();
        Date expDate = new Date(now.getTime() + expiration);

        return JWT.create()
                .withClaim("code", code) // 添加自定义字段
                .withIssuedAt(now) // 设置签发时间
                .withExpiresAt(expDate) // 设置过期时间
                .sign(Algorithm.HMAC256(secret)); // 使用密钥签名
    }

    /**
     * @param token 含有code的token
     * @return 提取code字段
     */
    public String resolveJwt(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT verify = jwtVerifier.verify(token); // 验证 token 签名和过期
            String code = verify.getClaim("code").asString(); // 提取 "code" 字段

            if (code == null || code.isEmpty()) {
                throw new BaseException(ErrorEnum.COMMON_ERROR);
            }

            return code;

        } catch (TokenExpiredException e) {
            throw new BaseException(ErrorEnum.EXPIRED_LOGIN);
        } catch (Exception e) {
            throw new BaseException(ErrorEnum.TOKEN_ERROR);
        }
    }
}
