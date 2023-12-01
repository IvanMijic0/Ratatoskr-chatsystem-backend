package ba.nosite.chatsystem.core.services.authServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static ba.nosite.chatsystem.helpers.TimeConversion.convertHourToMs;

@Service
public class JwtService {
    @Value("${authentication.token.secret.key}")
    String jwtSecretKey;

    @Value("${authentication.token.expirationHours}")
    Long jwtExpirationHours;

    @Value("${authentication.token.refreshExpirationHours}")
    Long refreshJwtExpirationHours;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractCustomClaim(String token, String claimName) {
        return extractClaim(token, claims -> claims.get(claimName, String.class));
    }

    public String generateTokenWithAdditionalClaims(Map<String, Object> customClaims, UserDetails userDetails) {
        long expirationHours = (jwtExpirationHours != null) ? jwtExpirationHours : 1L;
        return generateToken(customClaims, userDetails, convertHourToMs(expirationHours));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, convertHourToMs(jwtExpirationHours));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, convertHourToMs(refreshJwtExpirationHours));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
