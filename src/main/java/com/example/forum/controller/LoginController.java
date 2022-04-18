package com.example.forum.controller;

import com.example.forum.model.User;
import com.example.forum.model.dto.LoginUserDTO;
import com.example.forum.security.jwt.JwtProvider;
import com.example.forum.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LoginController {

    AuthenticationManager authenticationManager;

    JwtProvider jwtProvider;

    UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        System.out.println("hello");
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute(name = "loginUserDTO") LoginUserDTO userDTO, Model model) {
        try {
            String email = userDTO.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, userDTO.getPassword()));
            User user = userService.findByEmail(email);

            if (user == null) {
                model.addAttribute("invalidCredentials", true);
                return "login";
            }

            String token = jwtProvider.createToken(email, user.getRole().name());

            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            response.put("token", token);

        } catch (AuthenticationException e) {
            model.addAttribute("invalidCredentials", true);
            return "login";
        }
        return "index";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response,null);

        return "login";
    }
}
