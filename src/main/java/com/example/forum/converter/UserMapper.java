package com.example.forum.converter;

import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.model.dto.UserRegistrationDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;

        Converter<String, String> encodingPassword = (src) -> passwordEncoder.encode(src.getSource());

        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapper -> mapper
                        .using(encodingPassword)
                        .map(UserDTO::getPassword, User::setPassword));
        modelMapper.createTypeMap(UserRegistrationDTO.class, User.class)
                .addMappings(mapper -> mapper
                        .using(encodingPassword)
                        .map(UserRegistrationDTO::getPassword, User::setPassword));
    }

    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public User toEntity(UserRegistrationDTO userRegistrationDTO) {
        return modelMapper.map(userRegistrationDTO, User.class);
    }

    public UserDTO toUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public UserRegistrationDTO toUserRegistrationDTO(User user) {
        return modelMapper.map(user, UserRegistrationDTO.class);
    }

    public List<User> toEntities(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<UserDTO> toDTOs(List<User> users) {
        return users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

}
