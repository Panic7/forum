package com.example.forum.controller;

import com.example.forum.model.Topic;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.service.CategoryService;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/")
    public String getTopicList(Model model) {
        List<TopicDTO> topic = topicService.findAll();
        model.addAttribute("topics", topic);
        return "index";
    }
}
