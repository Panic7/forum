package com.example.forum.test;

import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.model.dto.UserRegistrationDTO;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.UserService;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.ObjectError;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class UserServiceIntegrationTest {
    private User user;
    private User admin;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
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
        admin = User.builder()
                .id(2)
                .username("testUser1")
                .email("testUser@example.com1")
                .password("password1")
                .role(Role.ROLE_ADMIN)
                .status(Status.ACTIVE)
                .pictureUrl("http://example.com/picture1.jpg")
                .build();
        userRepository.saveAll(List.of(user, admin));
    }
    
    @Test
    void whenFindById_thenReturnUserDTO() {
        UserDTO found = userService.findById(user.getId());
        assertThat(found.getUsername())
                .isEqualTo(user.getUsername());
    }
    
    @Test
    void whenFindAll_thenReturnUserDTOList() {
        List<UserDTO> found = userService.findAll();
        assertThat(found).hasSize(2);
    }
    
    @Test
    void whenSaveUserDTO_thenUserIsCreated() {
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("newUser");
        newUserDTO.setEmail("newUser@example.com");
        newUserDTO.setPassword("password");
        
        userService.save(newUserDTO, new MockHttpServletRequest());
        
        Optional<User> found = userRepository.findByUsername("newUser");
        assertThat(found).isPresent();
    }
    
    @Test
    void whenSaveUserRegistrationDTO_thenUserIsCreated() {
        UserRegistrationDTO newUserRegistrationDTO = new UserRegistrationDTO();
        newUserRegistrationDTO.setUsername("newUser");
        newUserRegistrationDTO.setEmail("newUser@example.com");
        newUserRegistrationDTO.setPassword("password");
        newUserRegistrationDTO.setConfirmPassword("password");
        
        Optional<ObjectError> result = userService.save(newUserRegistrationDTO);
        
        Optional<User> found = userRepository.findByUsername("newUser");
        assertThat(found).isPresent();
        assertTrue(result.isEmpty());
    }
    
    @Test
    void whenDeleteById_thenUserIsDeleted() {
        
        // when
        userService.deleteById(user.getId());
        
        // then
        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found).isNotPresent();
    }
    
    @Test
    void whenUpdateStatus_thenStatusIsUpdated() {
        userService.update(Status.BANNED, user.getId());
        
        Optional<User> found = userRepository.findById(user.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(Status.BANNED);
    }
    
    @Test
    void whenUpdateRole_thenRoleIsUpdated() {
        userService.update(Role.ROLE_ADMIN, user.getId());
        
        // then
        Optional<User> found = userRepository.findById(user.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ROLE_ADMIN);
    }
    
}