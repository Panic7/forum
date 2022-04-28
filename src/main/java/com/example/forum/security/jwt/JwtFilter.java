package com.example.forum.security.jwt;

import com.example.forum.common.StringConstants;
import com.example.forum.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    JwtProvider jwtProvider;
    UserDetailsServiceImpl userDetailsService;
    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> paths = List.of(StringConstants.SKIP_URLS);
        String requestURI = request.getRequestURI();
        return paths.stream().anyMatch(p -> pathMatcher.match(p, requestURI));
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        logger.info("Request URL path : " + request.getRequestURI());
        logger.info("Request content type: " + request.getContentType());
        String token = parseJwt(request);
        String username = null;
        if (token != null) {
            try {
                username = jwtProvider.extractUsername(token);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpStatus.SC_BAD_REQUEST, "Unable to get JWT Token");
                logger.warn("JWT Token has expired");
            } catch (ExpiredJwtException e) {
                response.sendError(HttpStatus.SC_BAD_REQUEST, "JWT Token has expired");
                logger.warn("JWT Token has expired");
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String, or unable to get header from request");
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        return null;
    }
}
