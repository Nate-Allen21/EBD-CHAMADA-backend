package br.com.admig.ebd.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "EBDADMIGRFII2026SECRETKEY1234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L);
    }

    @Test
    void generateTokenShouldIncludeUsernameAndBeValid() {
        UserDetails userDetails = User.withUsername("admin")
                .password("admin123")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValidShouldThrowWhenTokenHasExpired() {
        UserDetails userDetails = User.withUsername("admin")
                .password("admin123")
                .authorities("ROLE_ADMIN")
                .build();

        String expiredToken = Jwts.builder()
                .subject("admin")
                .issuedAt(new Date(System.currentTimeMillis() - 60_000))
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
