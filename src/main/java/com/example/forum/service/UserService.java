package com.example.forum.service;

import com.example.forum.common.StringConstants;
import com.example.forum.converter.UserMapper;
import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.model.dto.UserRegistrationDTO;
import com.example.forum.repository.UserRepository;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.jwt.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserService {

    CloudinaryService cloudinaryService;
    JwtService jwtService;
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    MailSenderService mailSenderService;

    public UserDTO findById(Integer id) {
        return userMapper.toUserDTO(userRepository.findById(id).orElseThrow());
    }

    public List<UserDTO> findAll() {
        return userMapper.toDTOs(userRepository.findAll());
    }


    public void save(UserDTO userDTO, HttpServletRequest request) {
        User user = userMapper.toEntity(userDTO);
        if (Objects.isNull(user.getRole())) {
            user.setRole(Role.ROLE_USER);
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        String siteURL = request.getRequestURL().toString();
        String url = siteURL.replace(request.getServletPath(), "");

        String mailBody = String.format(StringConstants.MAIL_BODY_ADMIN_REGISTERED,
                user.getEmail(), user.getUsername(), userDTO.getPassword(), url);
        mailSenderService.sendSimpleMessage(user.getEmail(),
                StringConstants.MAIL_SUBJECT_ADMIN_REGISTERED,
                mailBody);
    }

    public Optional<ObjectError> save(UserRegistrationDTO userDTO) {
        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            return Optional.of(new ObjectError(
                    "confirmPassword", "Passwords do not match"));
        }
        User user = userMapper.toEntity(userDTO);
        user.setStatus(Status.BANNED);
        userRepository.save(user);
        mailSenderService.sendSimpleMessage(user.getEmail(),
                StringConstants.MAIL_SUBJECT_USER_SUCCESSFUL_REGISTRATION,
                StringConstants.MAIL_BODY_USER_SUCCESSFUL_REGISTRATION);
        return Optional.empty();
    }

    public void deleteById(Integer id) {

        userRepository.deleteById(id);
    }

    @Transactional
    public void update(Status status, Integer id) {
        userRepository.setUserStatusById(status, id);
    }

    @Transactional
    public void update(Role role, Integer id) {
        userRepository.setUserRoleById(role, id);
    }


    public Map<String, Object> changeAvatar(MultipartFile file,
                                            Integer id,
                                            RedirectAttributes redirectAttributes,
                                            HttpServletRequest req,
                                            HttpServletResponse res) {
        try{
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
                return Map.of("redirectAttributes", redirectAttributes);
            }

            String url = cloudinaryService.uploadFile(file);
            if (url.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "There was an error, picture wasn't save, try again");
                return Map.of("redirectAttributes", redirectAttributes);
            }
            User user = userRepository.findById(id).orElseThrow();
            user.setPictureUrl(url);
            userRepository.save(user);
            final UserDetailsImpl userDetails = UserDetailsImpl.create(user);
            res = jwtService.updateJwtInCookies(res, req, userDetails);

            redirectAttributes.addFlashAttribute("message", "avatar changed successfully");
            return Map.of("response", res,
                    "redirectAttributes", redirectAttributes);
        } catch (RequestRejectedException ignored) {
            return Map.of("redirectAttributes", redirectAttributes);
        }
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
                        return redirectAttributes.addFlashAttribute("message", "Password length must be >= 5 and <= 16 symbols");
                    }
                } else {return redirectAttributes.addFlashAttribute("message","New password and current password must be different");
                }
            } else {
                return redirectAttributes.addFlashAttribute("message", "The entered current password and the actual password do not match");
            }
        } else {
            return redirectAttributes.addFlashAttribute("message", "The entered password and the actual password do not match");
        }
    }

}
