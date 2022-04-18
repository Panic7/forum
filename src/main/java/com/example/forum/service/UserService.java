package com.example.forum.service;

import com.example.forum.model.User;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserService {

    UserRepository userRepository;
    ModelMapper modelMapper;

    public User findByName(String name){
        return userRepository.findByName(name).orElseThrow();
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow();
    }

/*    public List<UserDTO> findAll() {
        return toDTOList(topicRepository.findAll());
    }*/

    public UserDTO toDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setPicture(user.getPicture());

        return userDTO;
    }

/*    User toEntity(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.Class);

        user.set
    }*/

}
