package com.example.forum.model.dto;

import com.example.forum.model.Picture;
import com.example.forum.model.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    //@NotBlank(message = "Enter the name")
    //@Size(min=4, max = 30, message = "Name must be of 4 to 30 symbols long ")
    String name;

    Picture picture;
}
