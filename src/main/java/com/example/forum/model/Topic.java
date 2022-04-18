package com.example.forum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
}
