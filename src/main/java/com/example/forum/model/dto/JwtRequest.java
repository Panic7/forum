package com.example.forum.model.dto;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtRequest implements Serializable {
    @NotBlank(message = "Enter username or email")
    String login;

    @Size(min = 5, max = 16, message = "Password length must be of 5 to 16 symbols")
    String password;
}