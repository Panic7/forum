package com.example.forum.service;

import com.example.forum.converter.UserMapper;
import com.example.forum.model.Status;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public UserDTO findById(Integer id) {
        return userMapper.toDTO(userRepository.findById(id).orElseThrow());
    }

    public void save(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new IllegalArgumentException("id must be null");
        }

        userRepository.save(userMapper.toEntity(userDTO));
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public void update(UserDTO userDto) {
        if (userDto.getId() == null) throw new IllegalArgumentException("id was null");

        userRepository.save(userMapper.toEntity(userDto));
    }


}
