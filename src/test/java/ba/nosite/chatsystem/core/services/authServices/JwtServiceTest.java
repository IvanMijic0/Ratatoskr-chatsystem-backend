package ba.nosite.chatsystem.core.services.authServices;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class JwtServiceTest {
    @Test
    public void testExtractUsername() {
        JwtService jwtService = new JwtService();
        String token = "your_test_token";
        String username = "testUser";

        JwtService spyJwtService = spy(jwtService);
        doReturn(username).when(spyJwtService).extractClaim(eq(token), any());

        assertEquals(username, spyJwtService.extractUsername(token));
    }

    @Test
    public void testGenerateToken() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = new User("testUser", "testPassword", Collections.emptyList());

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testGenerateTokenWithAdditionalClaims() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = new User("testUser", "testPassword", Collections.emptyList());

        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("customKey", "customValue");

        String token = jwtService.generateTokenWithAdditionalClaims(customClaims, userDetails);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("customValue", jwtService.extractCustomClaim(token, "customKey"));
    }

    @Test
    public void testIsTokenExpired() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = new User("testUser", "testPassword", Collections.emptyList());

        String expiredToken = jwtService.generateToken(userDetails);
        String validToken = jwtService.generateRefreshToken(userDetails);

        jwtService.extractAllClaims(expiredToken).setExpiration(new Date(System.currentTimeMillis() - 1000));

        assertTrue(jwtService.isTokenExpired(expiredToken));
        assertFalse(jwtService.isTokenExpired(validToken));
    }
}
