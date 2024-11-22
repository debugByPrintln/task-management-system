package com.melnikov.taskmanagementsystem.jwt;

import com.melnikov.taskmanagementsystem.exception.auth.InvalidJwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider.setSecretKey("testSecretKey");
        jwtTokenProvider.setValidityInMilliseconds(3600000);
    }

    @Test
    public void testCreateToken() {
        String token = jwtTokenProvider.createToken("testUser", "ROLE_USER");
        assertNotNull(token);

        Claims claims = Jwts.parser().setSigningKey("testSecretKey").parseClaimsJws(token).getBody();
        assertEquals("testUser", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role"));
    }

    @Test
    public void testValidateToken() {
        String token = jwtTokenProvider.createToken("testUser", "ROLE_USER");
        assertTrue(jwtTokenProvider.validateToken(token));

        assertThrows(InvalidJwtAuthenticationException.class, () -> {
            jwtTokenProvider.validateToken("invalidToken");
        });
    }

    @Test
    public void testGetUsername() {
        String token = jwtTokenProvider.createToken("testUser", "ROLE_USER");
        String username = jwtTokenProvider.getUsername(token);
        assertEquals("testUser", username);
    }
}
