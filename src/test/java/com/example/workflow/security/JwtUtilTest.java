package com.example.workflow.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();


    @Test
    void generateAndParse_shouldReturnValidClaims() {

        String token = jwtUtil.generate("john", "ROLE_ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtUtil.parse(token);

        assertEquals("john", claims.getSubject());
        assertEquals("ROLE_ADMIN", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }


    @Test
    void parse_shouldThrowForInvalidToken() {

        String invalidToken = "this.is.not.valid";

        assertThrows(Exception.class, () -> jwtUtil.parse(invalidToken));
    }


    @Test
    void parse_shouldFailIfTokenModified() {

        String token = jwtUtil.generate("john", "ROLE_USER");


        String tampered = token.substring(0, token.length() - 2) + "aa";

        assertThrows(Exception.class, () -> jwtUtil.parse(tampered));
    }
}
