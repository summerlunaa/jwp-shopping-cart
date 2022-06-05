package woowacourse.auth.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.Test;
import woowacourse.auth.exception.AuthException;

class JwtTokenProviderTest {

    private static final String SECRET_KEY = "anythinganythinganythinganythinganything";
    private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            SECRET_KEY, 360000);

    @Test
    void createToken() {
        String payload = "email@email.com";

        assertThat(jwtTokenProvider.createToken(payload)).isNotNull();
    }

    @Test
    void getPayload() {
        String payload = "email@email.com";
        String token = jwtTokenProvider.createToken(payload);

        assertThat(jwtTokenProvider.getPayload(token)).isEqualTo(payload);
    }

    @Test
    void getPayloadExpiredToken() {
        String payload = "email@email.com";
        Date validity = new Date(new Date().getTime() - 1);
        String token = Jwts.builder()
                .setSubject(payload)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        assertThatThrownBy(() -> jwtTokenProvider.getPayload(token))
                .isInstanceOf(AuthException.class)
                .hasMessage("만료된 토큰입니다.");
    }

    @Test
    void getPayloadErrorToken() {
        String token = "error.token.anything";

        assertThatThrownBy(() -> jwtTokenProvider.getPayload(token))
                .isInstanceOf(AuthException.class)
                .hasMessage("유효하지 않은 인증입니다.");
    }
}
