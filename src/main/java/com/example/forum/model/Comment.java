package com.example.forum.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @Column(name = "is_anonymous", nullable = false)
    boolean isAnonymous;

    @ManyToOne
    @JsonBackReference(value = "user-comment")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JsonBackReference(value = "topic-comment")
    @JoinColumn(name = "topic_id")
    Topic topic;
}
