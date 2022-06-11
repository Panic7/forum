package com.example.forum.security.jwt;

import com.example.forum.model.dto.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인 유저 정보 쉽게 가져오기 위한 Argument Resolver
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginUserResolver implements HandlerMethodArgumentResolver {

    @Value("${cookie.jwt}")
    private String cookieName;

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter param, ModelAndViewContainer mvc, NativeWebRequest nreq,
                                  WebDataBinderFactory dbf) {
        final Map<String, Object> resolved = new HashMap<>();

        HttpServletRequest req = (HttpServletRequest) nreq.getNativeRequest();

        // If there is a token in the cookie, take it out and verify it and return the login information
        Arrays.stream(req.getCookies()).filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue).findFirst().ifPresent(token -> {
                    Map<String, Object> claims = jwtService.extractAllClaims(jwtService.resolveToken(token));

                    //@LoginUser Integer id
                    if (param.getParameterType().isAssignableFrom(Integer.class)) {
                        resolved.put("resolved", claims.get("id"));
                    }

                    // @LoginUser String username, @LoginUser String email
                    // @LoginUser String authorities @LoginUser String pictureUrl
                    if (param.getParameterType().isAssignableFrom(String.class)) {
                        resolved.put("resolved", claims.get(param.getParameterName()));
                    }

                    // @LoginUser JwtResponse jwtResponse
                    if (param.getParameterType().isAssignableFrom(JwtResponse.class)) {
                        resolved.put("resolved", JwtResponse.builder()
                                .id((Integer) claims.get("id"))
                                .username((String) claims.get("username"))
                                .pictureUrl((String) claims.get("pictureUrl"))
                                .build());
                    }
                });

        return resolved.get("resolved");
    }
}