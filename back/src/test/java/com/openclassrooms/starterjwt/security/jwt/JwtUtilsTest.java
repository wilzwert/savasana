package com.openclassrooms.starterjwt.security.jwt;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:08:46
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tag("Security")
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private MemoryAppender memoryAppender;

    @BeforeEach
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.ERROR);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    public void teardown() {
        memoryAppender.stop();
        memoryAppender = null;
    }

    @Test
    public void shouldGenerateValidJwtToken() {
        UserDetailsImpl testUserDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("abcd1234")
                .build();

        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotEmpty();
        long count = token.chars().filter(ch -> ch == '.').count();
        assertThat(count).isEqualTo(2);

    }

    @Test
    public void shouldGetUserNameFromJwtToken() {
        UserDetailsImpl testUserDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("abcd1234")
                .build();

        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        String token = jwtUtils.generateJwtToken(authentication);
        String extractedUserName = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(extractedUserName).isEqualTo(testUserDetails.getUsername());
    }

    @Test
    public void shouldValidateJwtToken() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 2000000);

        String validToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertThat(jwtUtils.validateJwtToken(validToken)).isTrue();
    }

    @Test
    public void shouldValidateGeneratedJwtToken() {
        UserDetailsImpl testUserDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("abcd1234")
                .build();

        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    public void shouldReturnFalseAndLogSignatureExceptionMessage() {
        String invalidSignatureToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "differentTestSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 2000000);
        boolean result = jwtUtils.validateJwtToken(invalidSignatureToken);

        assertThat(result).isFalse();
        assertThat(memoryAppender.contains("Invalid JWT signature:", Level.ERROR)).isTrue();
    }

    @Test
    public void shouldReturnFalseAndLogMalformedJwtExceptionMessage() {
        String malformedToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4";

        boolean result = jwtUtils.validateJwtToken(malformedToken);

        assertThat(result).isFalse();
        assertThat(memoryAppender.contains("Invalid JWT token:", Level.ERROR)).isTrue();
    }

    @Test
    public void shouldReturnFalseAndLogExpiredJwtExceptionMessage() {
        String invalidSignatureToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        boolean result = jwtUtils.validateJwtToken(invalidSignatureToken);

        assertThat(result).isFalse();
        assertThat(memoryAppender.contains("JWT token is expired:", Level.ERROR)).isTrue();
    }

    @Test
    public void shouldReturnFalseAndLogUnsupportedJwtExceptionMessage() {
        String invalidSignatureToken = Jwts.builder()
                .setSubject("testUser")
                .compact();

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        boolean result = jwtUtils.validateJwtToken(invalidSignatureToken);

        assertThat(result).isFalse();
        assertThat(memoryAppender.contains("JWT token is unsupported:", Level.ERROR)).isTrue();
    }
    @Test
    public void shouldReturnFalseAndLogIllegalArgumentExceptionMessage() {
        String emptyToken = "";

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        boolean result = jwtUtils.validateJwtToken(emptyToken);

        assertThat(result).isFalse();
        assertThat(memoryAppender.contains("JWT claims string is empty:", Level.ERROR)).isTrue();
    }
}
