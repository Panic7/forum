package com.example.forum.security.jwt;

import com.example.forum.model.dto.JwtResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoginUserInterceptor implements HandlerInterceptor {

    @Value("${cookie.jwt}")
    private String cookieName;

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ModelAndViewDefiningException {

        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName)).findFirst().map(Cookie::getValue)
                .orElse("empty");
        log.info("IT'S LOGIN_INTERCEPTOR");
        log.info("token : {}", token);

        try {
            // Set user information ‘verify’ in ‘request’ so that login information can be easily retrieved and used like ‘session.id’ in ‘View’
            JwtResponse jwtResponse = jwtService.extractResponse(token);

            // Accessible as `jwtResponse.email`, `jwtResponse.id` in `view`
            request.setAttribute("jwtResponse", jwtResponse);
        } catch (ExpiredJwtException ex) {
            log.error("token expired");

            ModelAndView mav = new ModelAndView("login");
            mav.addObject("return_url", request.getRequestURI());

            throw new ModelAndViewDefiningException(mav);
        } catch (JwtException ex) {
            log.error("bad token");

            ModelAndView mav = new ModelAndView("login");

            throw new ModelAndViewDefiningException(mav);
        }

        return true;
    }
}
