package com.example.forum.controller;

import com.example.forum.model.dto.JwtRequest;
import com.example.forum.model.dto.UserRegistrationDTO;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.UserDetailsServiceImpl;
import com.example.forum.security.jwt.JwtService;
import com.example.forum.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@Slf4j
public class LoginController {

    AuthenticationManager authenticationManager;
    UserDetailsServiceImpl userDetailsService;
    JwtService jwtService;
    UserService userService;

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("request") JwtRequest request) {
        return "user/login";
    }

    @GetMapping("/registry")
    public String registrationPage(@ModelAttribute("user") UserRegistrationDTO user) {
        return "user/registry";
    }


    @PostMapping("/login")
    public String createAuthenticationToken(@Valid @ModelAttribute("request") JwtRequest request,
                                            BindingResult result,
                                            HttpServletResponse res,
                                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result);
            model.addAttribute("request", new JwtRequest());
            return "user/login";
        }

        try {
            authenticate(request.getLogin(), request.getPassword());
        } catch (IllegalArgumentException e) {
            result.addError(new ObjectError("password", e.getMessage()));
            model.addAttribute("errors", result);
            model.addAttribute("request", new JwtRequest());
            return "user/login";
        }
        final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService
                .loadUserByUsername(request.getLogin());
        jwtService.saveJwtInCookies(res, userDetails);

        return "redirect:/";
    }

    @PostMapping("/registry")
    public String registration(@Valid @ModelAttribute("user") UserRegistrationDTO user,
                                            BindingResult result,
                                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("errors", result);
            return "user/registry";
        }
        Optional<ObjectError> error;
        try{
            error = userService.save(user);
        } catch (Exception e) {
            result.addError(new ObjectError("email", "User with this email is now existed"));
            model.addAttribute("user", user);
            model.addAttribute("errors", result);
            return "user/registry";
        }

        if(error.isPresent()) {
            result.addError(error.get());
            model.addAttribute("user", user);
            model.addAttribute("errors", result);
            return "user/registry";
        }
        return "user/registry-success";
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new IllegalArgumentException("User disabled", e);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Incorrect login or password", e);
        }
    }
}
