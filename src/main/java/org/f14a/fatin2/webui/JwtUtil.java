package org.f14a.fatin2.webui;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtUtil {
    // 请务必将密钥放入配置文件，不要硬编码
    private static final String SECRET = "YourSuperSecretKey_Fatin2";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24小时

    public static String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer("fatin2-server")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    public static String verifyToken(String token) {
        // 如果验证失败会抛出 JWTVerificationException
        DecodedJWT jwt = JWT.require(ALGORITHM)
                .withIssuer("fatin2-server")
                .build()
                .verify(token);
        return jwt.getSubject();
    }
}