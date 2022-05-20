package com.example.forum.service;

import com.example.forum.converter.UserMapper;
import com.example.forum.model.Picture;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.repository.UserRepository;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.jwt.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserService {

    CloudinaryService cloudinaryService;
    JwtService jwtService;
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserDTO findById(Integer id) {
        return userMapper.toDTO(userRepository.findById(id).orElseThrow());
    }

    public void save(UserDTO userDTO) {
        userRepository.save(userMapper.toEntity(userDTO));
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void update(UserDTO userDto, Integer id) {
        User findedUser = userRepository.findById(id).orElseThrow();

        findedUser.setEmail(userDto.getEmail());
        findedUser.setUsername(userDto.getUsername());
        findedUser.setPassword(userDto.getPassword());
        findedUser.setRole(userDto.getRole());
        findedUser.setStatus(userDto.getStatus());
        if (userDto.getPictureUrl() != null) {
            Picture picture = new Picture();
            picture.setUrl(userDto.getPictureUrl());
            findedUser.setPicture(picture);
        }
    }

    public Map<String, Object> changeAvatar(MultipartFile file,
                                            Integer id,
                                            RedirectAttributes redirectAttributes,
                                            HttpServletRequest req,
                                            HttpServletResponse res) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return Map.of("redirectAttributes", redirectAttributes);
        }

        String url = cloudinaryService.uploadFile(file);
        if (url.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "There was an error, picture wasn't save, try again");
            return Map.of("redirectAttributes", redirectAttributes);
        }
        Picture picture = new Picture();
        picture.setUrl(url);
        User user = userRepository.findById(id).orElseThrow();
        user.setPicture(picture);
        userRepository.save(user);
        final UserDetailsImpl userDetails = (UserDetailsImpl) UserDetailsImpl.create(user);
        res = jwtService.updateJwtInCookies(res, req, userDetails);

        redirectAttributes.addFlashAttribute("message", "avatar changed successfully");
        return Map.of("response", res,
                "redirectAttributes", redirectAttributes);
    }

    public RedirectAttributes changePassword(RedirectAttributes redirectAttributes,
                                             String currPass,
                                             String newPass,
                                             String confirmNewPass,
                                             Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        if (currPass != null && newPass != null && confirmNewPass != null) {
            if (passwordEncoder.matches(currPass, user.getPassword())) {
                if (!currPass.equals(newPass)) {
                    if (newPass.length() >= 5 && newPass.length() <= 16) {
                        if (newPass.equals(confirmNewPass)) {

                            user.setPassword(passwordEncoder.encode(newPass));
                            userRepository.save(user);
                            redirectAttributes.addFlashAttribute("message", "password changed successfully");
                            return redirectAttributes;

                        } else {
                            return redirectAttributes.addFlashAttribute("message","Confirm new password was failed");
                        }
                    } else {
                        return redirectAttributes.addFlashAttribute("message", "Password length must be >= 5 and <= 16 symbols.");
                    }
                } else {return redirectAttributes.addFlashAttribute("message","New password and current password must be different.");
                }
            } else {
                return redirectAttributes.addFlashAttribute("message", "The entered current password and the actual password do not match");
            }
        } else {
            return redirectAttributes.addFlashAttribute("message", "The entered password and the actual password do not match");
        }
    }


}
