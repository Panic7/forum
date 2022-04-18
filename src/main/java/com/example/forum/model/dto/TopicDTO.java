package com.example.forum.model.dto;

import com.example.forum.model.Category;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicDTO {

    @NotBlank(message = "Enter the title")
    @Size(min=4, max = 100, message = "Title must be of 4 to 100 symbols long")
    String header;

    @Size(min=1, max = 2000, message = "Text must be of 1 to 2000 symbols long")
    String description;

    @NotBlank(message = "Is topic anonym?")
    boolean isAnonymous;

    LocalDateTime creationDate;

    UserDTO userDTO;

    @NotBlank(message = "Choose the category")
    CategoryDTO categoryDTO;
}
