package com.example.forum.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String header;

    @Column(nullable = false)
    String description;

    @Column(name = "is_anonymous", nullable = false)
    boolean isAnonymous;

    @Column(nullable = false)
    Integer views;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "topic-comment")
    List<Comment> comments;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
}
