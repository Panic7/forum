package com.example.forum.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank(message = "Enter the name")
    @Size(min = 4, max = 30, message = "Name must be of 4 to 30 symbols long")
    @Column(unique = true, nullable = false)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @NotBlank(message = "Password must be at least 5 characters long")
    String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Status status;

    String pictureUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference(value = "user-comment")
    List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.user", cascade=CascadeType.ALL)
    Set<TopicMark> topicMarks = new HashSet<>();

}
