package org.pranav.identityservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.pranav.identityservice.entity.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET = "7A24432646294A404E635266556A586E327235753878214125442A472D4B6150";

    /**
     * Calls the create token method by creating claims (which are additional payload parameters which is optional)
     *
     * @param userInfo UserInfo Object
     * @return token
     */
    public String generateToken(UserInfo userInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("emailId", userInfo.getEmailId());
        claims.put("roles", userInfo.getRoles());
        claims.put("name", userInfo.getName());
        return createToken(claims, userInfo.getUsername());
    }

    /**
     * JWT token creation which has 3 parts namely Header, Payload & verify Signature
     * as found in <a href="https://jwt.io/">...</a>
     *
     * @param claims
     * @param username
     * @return
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 1 hr expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Verify signature
     *
     * @return
     */
    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }


    public boolean  validateToken(String token, String inputUsername) {
        final String username = extractUsernameFromToken(token);
        return (username.equals(inputUsername) && !isTokenExpired(token));
    }
    public void validateToken1(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public String extractUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpirationFromToken(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationFromToken(token).before(new Date());
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsFunc) {
        final Claims claims = extractAllClaims(token);
        return claimsFunc.apply(claims);
    }

    /**
     * Extract claims (payload) from the token
     *
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
