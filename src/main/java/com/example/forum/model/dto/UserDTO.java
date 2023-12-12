package com.example.forum.model.dto;

import com.example.forum.model.Role;
import com.example.forum.model.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    Integer id;

    @Size(min=4, max = 30, message = "Username must be of 4 to 16 symbols long")
    String username;

    @Email(message = "Email is not valid")
    String email;

    @Size(min = 5, max = 16, message = "Password length must be of 5 to 16 symbols")
    String password;

    @NotNull(message = "Value cannot be null")
    Role role;

    Status status;

    String pictureUrl;

}
