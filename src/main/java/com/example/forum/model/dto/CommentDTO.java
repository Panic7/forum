package com.example.forum.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {

    Integer id;

    @Size(min = 6, max = 300, message = "Text must be of 6 to 300 symbols long")
    String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime creationDate;

    String sinceCreation;

    boolean isAnonymous;

    UserDTO userDTO;

    Integer topicId;
}
