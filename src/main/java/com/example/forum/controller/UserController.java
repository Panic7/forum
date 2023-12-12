package com.example.forum.controller;

import com.example.forum.common.StringConstants;
import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.security.jwt.LoginUser;
import com.example.forum.service.MailSenderService;
import com.example.forum.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/user")
@Slf4j
@Controller
public class UserController {
    UserService userService;
    MailSenderService mailSenderService;

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public String getAddView(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/user-add";
    }

    @GetMapping("/management")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/user-management";
    }

    @GetMapping("/profile")
    public String userProfile() {
        return "user/profile";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public String addUser(HttpServletRequest request,
                          @Valid @ModelAttribute("user") UserDTO user,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("errors", result);
            return "user/user-add";
        }
        userService.save(user, request);
        return "redirect:/user/management";
    }

    @PutMapping("/profile/change-avatar")
    public String changeAvatar(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes,
                               @LoginUser Integer id,
                               HttpServletRequest req,
                               HttpServletResponse res) {
        Map<String, Object> map = userService.changeAvatar(file, id, redirectAttributes, req, res);
        res = (HttpServletResponse) map.get("response");
        redirectAttributes = (RedirectAttributes) map.get("redirectAttributes");

        return "redirect:/user/profile";
    }

    @PutMapping("/profile/change-password")
    public String changePassword(@RequestParam("currPass") String currPass,
                                 @RequestParam("newPass") String newPass,
                                 @RequestParam("confirmNewPass") String confirmNewPass,
                                 @LoginUser Integer id,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes = userService.changePassword(redirectAttributes, currPass, newPass, confirmNewPass, id);
        return "redirect:/";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public String setStatusOrRole(@RequestParam(name = "userStatus", required = false) Status status,
                                  @RequestParam(name = "userRole", required = false) Role role,
                                  @RequestParam(name = "email") String email,
                                  @PathVariable Integer id,
                                  HttpServletRequest request) {
        if (!Objects.isNull(status)) {
            userService.update(status, id);
            if (status.equals(Status.ACTIVE)) {
                String siteURL = request.getRequestURL().toString();
                String url = siteURL.replace(request.getServletPath(), "");

                String mailBody = String.format(StringConstants.MAIL_BODY_ACCESS_UNBLOCKED, url);
                mailSenderService.sendSimpleMessage(email,
                        StringConstants.MAIL_SUBJECT_ACCESS_UNBLOCKED,
                        mailBody);
            } else {
                if (status.equals(Status.BANNED)){
                    String siteURL = request.getRequestURL().toString();
                    String url = siteURL.replace(request.getServletPath(), "");

                    String mailBody = String.format(StringConstants.MAIL_BODY_ACCESS_BLOCKED, url);
                    mailSenderService.sendSimpleMessage(email,
                            StringConstants.MAIL_SUBJECT_ACCESS_BLOCKED,
                            mailBody);
                }
            }
        } else {
            if (!Objects.isNull(role)) {
                userService.update(role, id);
            }
        }
        return "redirect:/user/management";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return "redirect:/user/management";
    }
}
