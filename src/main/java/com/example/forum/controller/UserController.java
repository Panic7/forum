package com.example.forum.controller;

import com.example.forum.security.jwt.LoginUser;
import com.example.forum.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/user")
@Slf4j
@Controller
public class UserController {
    UserService userService;

    @GetMapping("/profile")
    public String userProfile() {
        return "user/profile";
    }

    @PostMapping("/profile/change-avatar")
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

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currPass") String currPass,
                                 @RequestParam("newPass") String newPass,
                                 @RequestParam("confirmNewPass") String confirmNewPass,
                                 @LoginUser Integer id,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes = userService.changePassword(redirectAttributes,currPass,newPass,confirmNewPass,id);
        return "redirect:/user/profile";
    }
}
