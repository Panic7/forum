package com.example.forum.model.dto;

import com.example.forum.model.Status;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatusChangeDTO {

    Integer id;

    Status status;
}
