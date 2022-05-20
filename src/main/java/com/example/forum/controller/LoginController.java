package com.example.forum.controller;

import com.example.forum.model.dto.JwtRequest;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.UserDetailsServiceImpl;
import com.example.forum.security.jwt.JwtService;
import lombok.AccessLevel;
import lombok.experimental.NonFinal;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@Slf4j
public class LoginController {

    AuthenticationManager authenticationManager;

    UserDetailsServiceImpl userDetailsService;

    JwtService jwtService;

    @GetMapping(path = {"/login", "/"})
    public String loginPage(@ModelAttribute("request") JwtRequest request) {
        return "login";
    }

    @PostMapping("/login")
    public String createAuthenticationToken(@Valid @ModelAttribute("request") JwtRequest request,
                                            BindingResult result,
                                            HttpServletResponse res) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            authenticate(request.getLogin(), request.getPassword());
        } catch (IllegalArgumentException e) {
            return "login";
        }
        final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService
                    .loadUserByUsername(request.getLogin());
        jwtService.saveJwtInCookies(res, userDetails);

        return "redirect:/dashboard";
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new IllegalArgumentException("User disabled", e);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect login or password", e);
        } catch(AuthenticationException e) {
            throw new IllegalArgumentException();
        }
    }
}
