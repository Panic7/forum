package com.example.forum.controller;

import com.example.forum.model.Topic;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.service.CategoryService;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
public class TopicController {

    private final TopicService topicService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/topic")
    public String getTopicList(Model model) {
        List<TopicDTO> topic = topicService.findAll();
        model.addAttribute("topics", topic);
        return "index";
    }
}
