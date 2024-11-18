package com.openclassrooms.starterjwt.security.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:18/11/2024
 * Time:09:03
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class AuthTokenFilterIT {

    @Value("${oc.app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnUnauthorizedWhenJwtTokenSignatureInvalid() throws Exception {
        String invalidSignatureToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        mockMvc.perform(get("/api/user/1")
                        .header("Authorization", "Bearer " + invalidSignatureToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedWhenJwtTokenMalformed() throws Exception {

        mockMvc.perform(get("/api/user/1")
                        .header("Authorization", "Bearer yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void shouldReturnUnauthorizedWhenJwtTokenExpired() throws Exception {
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        mockMvc.perform(get("/api/user/1")
                .header("Authorization", "Bearer " + expiredToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedWhenJwtTokenUnsupported() throws Exception {
        String unsupportedToken = Jwts.builder()
                .setSubject("testUser")
                .compact();

        mockMvc.perform(get("/api/user/1")
                        .header("Authorization", "Bearer " + unsupportedToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedWhenJwtTokenEmpty() throws Exception {
        mockMvc.perform(get("/api/user/1")
                        .header("Authorization", "Bearer "))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}