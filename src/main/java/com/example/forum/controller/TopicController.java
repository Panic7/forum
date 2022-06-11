package com.example.forum.controller;


import com.example.forum.model.TopicMark;
import com.example.forum.model.dto.CommentDTO;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.security.jwt.LoginUser;
import com.example.forum.service.CategoryService;
import com.example.forum.service.CommentService;
import com.example.forum.service.TopicMarkService;
import com.example.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class TopicController {

    private static final String DEFAULT_SORT_PARAM = "creationDate";
    private static final int FIRST_PAGE_NUMBER = 1;
    private final TopicService topicService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final TopicMarkService topicMarkService;

    @GetMapping(value = {"/home", ""})
    public String getAllPageWithPagination(@RequestParam(name = "sortParam", defaultValue = DEFAULT_SORT_PARAM) String sortParam,
                                           @RequestParam(required = false, name = "category") Integer categoryId,
                                           @LoginUser Integer userId,
                                           Model model) {
        model.addAttribute(sortParam);
        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
            return getOnePageWithCategoryFilter(sortParam, categoryId, userId, FIRST_PAGE_NUMBER, model);
        }
        return getOnePage(sortParam, userId, FIRST_PAGE_NUMBER, model);
    }

    @GetMapping("/home/page/{pageNumber}/{categoryId}")
    public String getOnePageWithCategoryFilter(@RequestParam(name = "sortParam", defaultValue = DEFAULT_SORT_PARAM) String sortParam,
                                               @PathVariable Integer categoryId,
                                               @LoginUser Integer userId,
                                               @PathVariable("pageNumber") int currentPage,
                                               Model model) {
        Page<TopicDTO> page = topicService.findPage(currentPage, sortParam, categoryId);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<TopicDTO> topics = page.getContent();
        topics.forEach(t -> topicService.actualizeDataDTO(t, userId));

        model.addAttribute("topics", topics);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortParam", sortParam);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("firstPage", FIRST_PAGE_NUMBER);
        return "index";
    }

    @GetMapping("/home/page/{pageNumber}")
    public String getOnePage(@RequestParam(name = "sortParam", defaultValue = DEFAULT_SORT_PARAM) String sortParam,
                             @LoginUser Integer userId,
                             @PathVariable("pageNumber") int currentPage,
                             Model model) {
        Page<TopicDTO> page = topicService.findPage(currentPage, sortParam);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<TopicDTO> topics = page.getContent();
        topics.forEach(t -> topicService.actualizeDataDTO(t, userId));

        model.addAttribute("topics", topics);
        model.addAttribute("sortParam", sortParam);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("firstPage", FIRST_PAGE_NUMBER);
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
            return "redirect:/home";
        } else {
            return "topic/topic-add";
        }
    }

    @GetMapping("/topic/{id}")
    public String getTopic(@PathVariable("id") int topicId,
                           @LoginUser Integer userId,
                           Model model) {
        model.addAttribute("topic", topicService.actualizeDataDTO(
                topicService.findByIdEagerly(topicId), userId));
        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("mark", new TopicMark());
        return "topic/topic";
    }

    @PostMapping("/topic/{id}/mark-set")
    public String setMark(@RequestParam(name = "topicId") Integer topicId,
                          @RequestParam(name = "mark") Boolean mark,
                          @LoginUser Integer userId,
                          Model model) {
        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("mark", new TopicMark());
        topicMarkService.saveMark(mark, userId, topicId);
        model.addAttribute("topic", topicService.actualizeDataDTO(
                topicService.findByIdEagerly(topicId), userId));
        return "topic/topic";
    }

    @PostMapping("/topic/comment-add")
    public String addComment(@Valid @ModelAttribute("comment") CommentDTO commentDTO,
                             BindingResult result,
                             @LoginUser Integer userId,
                             Model model) {
        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("mark", new TopicMark());
        if (result.hasErrors()) {
            model.addAttribute("topic", topicService.actualizeDataDTO(
                    topicService.findByIdEagerly(commentDTO.getTopicId()), userId));
            return "topic/topic";
        }
        commentService.save(commentDTO, userId);
        model.addAttribute("topic", topicService.actualizeDataDTO(
                topicService.findById(commentDTO.getTopicId()), userId));

        return "topic/topic";
    }
}
