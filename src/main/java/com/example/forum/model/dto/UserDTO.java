package com.example.forum.model.dto;

import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    //@NotBlank(message = "Enter the name")
    //@Size(min=4, max = 30, message = "Name must be of 4 to 30 symbols long ")
    String username;

    @Email
    String email;

    @JsonIgnore
    String password;

    Role role;

    Status status;

    String pictureUrl;

}
