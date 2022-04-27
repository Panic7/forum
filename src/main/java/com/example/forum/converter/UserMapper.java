package com.example.forum.converter;

import com.example.forum.model.Picture;
import com.example.forum.model.User;
import com.example.forum.model.dto.UserDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.modelMapper = new ModelMapper();

        Converter<String, String> encodingPassword = (src) -> passwordEncoder.encode(src.getSource());
        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMapping(UserDTO::getPictureUrl, User::setPicture)
                .addMappings(mapper -> mapper
                        .using(encodingPassword)
                        .map(UserDTO::getPassword, User::setPassword)
                );
        modelMapper.createTypeMap(Picture.class, String.class);
    }

    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
