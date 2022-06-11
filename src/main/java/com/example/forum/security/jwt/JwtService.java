package com.example.forum.security.jwt;

import com.example.forum.model.dto.JwtResponse;
import com.example.forum.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {
    @NonFinal
    @Value("${cookie.jwt}")
    String cookieJWT;

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        Map<String, Object> info = extractAllClaims(token);

        return (String) info.get("username");
    }

    public String extractEmail(String token) {
        Map<String, Object> info = extractAllClaims(token);

        return (String) info.get("email");
    }

    public String extractAuthorities(String token) {
        Map<String, Object> info = extractAllClaims(token);

        return (String) info.get("authorities");
    }

    public JwtResponse extractResponse(String token) {

        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody();

        return JwtResponse.builder()
                .id((Integer) claims.get("id"))
                .username((String) claims.get("username"))
                .pictureUrl((String) claims.get("pictureUrl"))
                .build();

    }

    //for retrieveing any information from token we will need the secret key
    public Map<String, Object> extractAllClaims(String token) {
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
        claims.put("id", userDetails.getId());
        claims.put("username", userDetails.getUsername());
        claims.put("pictureUrl", userDetails.getPictureUrl());

        return createToken(claims);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization
    //   compaction of the JWT to a URL-safe string
    private String createToken(Map<String, Object> claims) {

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusHours(6)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    public void saveJwtInCookies(HttpServletResponse res,
                                 UserDetails userDetails) {
        Cookie cookie = new Cookie(cookieJWT,
                "Bearer_" + this.generateToken((UserDetailsImpl) userDetails));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        res.addCookie(cookie);
    }

    public HttpServletResponse updateJwtInCookies(HttpServletResponse res,
                                                  HttpServletRequest req,
                                                  UserDetails userDetails) {

        Cookie cookie = new Cookie(cookieJWT,
                "Bearer_" + this.generateToken((UserDetailsImpl) userDetails));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        res.addCookie(cookie);
        return res;
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }

        return null;
    }


}
