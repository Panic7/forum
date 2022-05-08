package com.example.forum.controller;

import com.example.forum.model.dto.JwtResponse;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.security.jwt.LoginUser;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class TopicController {

    private final TopicService topicService;

    //@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public String getTopicList(Model model) {
        model.addAttribute("topics", topicService.findAll());
        return "index";
    }
}
