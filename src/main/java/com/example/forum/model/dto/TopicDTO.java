package com.example.forum.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicDTO {

    @NotBlank(message = "Enter the title")
    @Size(min=4, max = 100, message = "Title must be of 4 to 100 symbols long")
    String header;

    @Size(min=20, max = 2000, message = "Text must be of 50 to 2000 symbols long")
    String description;

    boolean isAnonymous;

    Integer views;

    Integer rating;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime creationDate;

    String sinceCreation;

    CategoryDTO categoryDTO;

    UserDTO userDTO;

    List<CommentDTO> comments;
}
