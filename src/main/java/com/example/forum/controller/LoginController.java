package com.example.forum.controller;

import com.example.forum.model.dto.JwtRequest;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.UserDetailsServiceImpl;
import com.example.forum.security.jwt.JwtProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LoginController {

    AuthenticationManager authenticationManager;

    UserDetailsServiceImpl userDetailsService;

    JwtProvider jwtProvider;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("request") JwtRequest request) {
        return "login";
    }

    @GetMapping("/")
    public String start(Model model) {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String createAuthenticationToken(JwtRequest request,
                                            Model model) {
        authenticate(request.getUsername(), request.getPassword());

        final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService
                .loadUserByUsername(request.getUsername());

        final String token = jwtProvider.generateToken(userDetails);
        model.addAttribute("token", token);
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);

        return "login";
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new IllegalArgumentException("User disabled", e);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect username or password", e);
        }
    }
}
