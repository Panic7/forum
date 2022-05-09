package com.example.forum.security.jwt;

import com.example.forum.model.dto.JwtResponse;
import com.example.forum.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractAuthorities(String token) {
        Map<String, Object> info = extractAllClaims(token);

        return (String) info.get("authorities");
    }

    public JwtResponse extractResponse(String token) {

        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody();

        return JwtResponse.builder()
                .userEmail(claims.getSubject())
                .authorities((String) claims.get("authorities"))
                .build();

    }

    //for retrieveing any information from token we will need the secret key
    private Map<String, Object> extractAllClaims(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody();

        return new HashMap<>(claims);
    }

    //generate token for user
    public String generateToken(UserDetailsImpl userDetails) {
        final String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);

        return createToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization
    //   compaction of the JWT to a URL-safe string
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

}
