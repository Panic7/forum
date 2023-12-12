package com.example.forum.test;

import com.example.forum.converter.UserMapper;
import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.model.dto.UserRegistrationDTO;
import com.example.forum.repository.UserRepository;
import com.example.forum.security.UserDetailsImpl;
import com.example.forum.security.jwt.JwtService;
import com.example.forum.service.CloudinaryService;
import com.example.forum.service.MailSenderService;
import com.example.forum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MailSenderService mailSenderService;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private CloudinaryService cloudinaryService;
    
    @InjectMocks
    private UserService userService;
    
    private User user;
    private UserDTO userDTO;
    private UserRegistrationDTO userRegistrationDTO;
    
    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1)
                .username("testUser")
                .email("testUser@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .pictureUrl("http://example.com/picture.jpg")
                .build();
        
        userDTO = UserDTO.builder()
                .id(1)
                .username("testUser")
                .email("testUser@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .pictureUrl("http://example.com/picture.jpg")
                .build();
        
        userRegistrationDTO = UserRegistrationDTO.builder()
                .username("testUser")
                .email("testUser@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
    }
    
    @Test
    void testFindById() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);
        
        UserDTO result = userService.findById(1);
        
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(userMapper, times(1)).toUserDTO(any(User.class));
        assertNotNull(result);
    }
    
    @Test
    void testSaveNewUser() {
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mailSenderService).sendSimpleMessage
                (any(String.class),any(String.class),any(String.class));
        
        userService.save(userDTO, new MockHttpServletRequest());
        
        verify(userMapper, times(1)).toEntity(any(UserDTO.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(any(Integer.class));
        
        userService.deleteById(1);
        
        verify(userRepository, times(1)).deleteById(any(Integer.class));
    }
    
    @Test
    void testFindAll() {
        List<User> users = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTOs(anyList())).thenReturn(Collections.singletonList(userDTO));
        
        List<UserDTO> result = userService.findAll();
        
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTOs(anyList());
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testSaveUserRegistrationDTO() {
        when(userMapper.toEntity(any(UserRegistrationDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mailSenderService).sendSimpleMessage(any(String.class),any(String.class),any(String.class));
        
        Optional<ObjectError> result = userService.save(userRegistrationDTO);
        
        verify(userMapper, times(1)).toEntity(any(UserRegistrationDTO.class));
        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testUpdateStatus() {
        doNothing().when(userRepository).setUserStatusById(any(Status.class), any(Integer.class));
        
        userService.update(Status.ACTIVE, 1);
        
        verify(userRepository, times(1)).setUserStatusById(any(Status.class), any(Integer.class));
    }
    
    @Test
    void testUpdateRole() {
        doNothing().when(userRepository).setUserRoleById(any(Role.class), any(Integer.class));
        
        userService.update(Role.ROLE_ADMIN, 1);
        
        verify(userRepository, times(1)).setUserRoleById(any(Role.class), any(Integer.class));
    }
    
    @Test
    void testChangePassword() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String currPass = "oldPass";
        String newPass = "newPass";
        String confirmNewPass = "newPass";
        
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPass");
        
        RedirectAttributes result = userService.changePassword(redirectAttributes, currPass, newPass, confirmNewPass, 1);
        
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
        assertNotNull(result);
    }
    
    @Test
    void testChangeAvatar() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "hello.png", MediaType.IMAGE_PNG_VALUE, "Hello, World!".getBytes());
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse res = new MockHttpServletResponse();
        
        when(cloudinaryService.uploadFile(any(MultipartFile.class))).thenReturn("url");
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.updateJwtInCookies(any(HttpServletResponse.class), any(HttpServletRequest.class), any(UserDetailsImpl.class))).thenReturn(res);
        
        Map<String, Object> result = userService.changeAvatar(file, 1, redirectAttributes, req, res);
        
        verify(cloudinaryService, times(1)).uploadFile(any(MultipartFile.class));
        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).updateJwtInCookies(any(HttpServletResponse.class), any(HttpServletRequest.class), any(UserDetailsImpl.class));
        assertNotNull(result);
    }
    
    // Test cases will go here
}