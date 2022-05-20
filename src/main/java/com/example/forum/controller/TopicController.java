package com.example.forum.controller;

import com.example.forum.model.dto.TopicDTO;
import com.example.forum.security.jwt.LoginUser;
import com.example.forum.service.CategoryService;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@Slf4j
public class TopicController {

    private final TopicService topicService;
    private final CategoryService categoryService;

    @GetMapping("/dashboard")
    public String getTopicList(Model model) {
        model.addAttribute("topics", topicService.actualizeSinceCreation(topicService.findAll()));
        return "index";
    }

    @GetMapping("/topic/add")
    public String addTopic(Model model) {
        model.addAttribute("topic", new TopicDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "topic/topic-add";
    }

    @PostMapping("/topic/add")
    public String addTopic(@Valid @ModelAttribute("topic") TopicDTO topic,
                           BindingResult result,
                           @LoginUser Integer userId,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "topic/topic-add";
        }

        if (topicService.save(topic, userId)) {
            return "redirect:/dashboard";
        } else {
            return "topic/topic-add";
        }
    }
}
