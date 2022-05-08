package com.example.forum.security.jwt;

import com.example.forum.common.StringConstants;
import com.example.forum.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @NonFinal
    @Value("${cookie.jwt}")
    private String cookieName;
    JwtService jwtService;
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
                username = jwtService.extractUsername(token);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token");
                ModelAndView mav = new ModelAndView("login");

                throw new ModelAndViewDefiningException(mav);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired");
                ModelAndView mav = new ModelAndView("login");

                throw new ModelAndViewDefiningException(mav);
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
            logger.warn("JWT Token does not begin with Bearer_ String, or unable to get cookies");
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName)).findFirst().map(Cookie::getValue)
                .orElse("empty");

        log.info("token : {}", token);
        if (token != null && token.startsWith("Bearer_")) {
            return token.substring(7);
        }
        return null;
    }
}
