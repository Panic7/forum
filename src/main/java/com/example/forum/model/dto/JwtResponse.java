package com.example.forum.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtResponse {
    String userEmail;
    String authorities;
}
