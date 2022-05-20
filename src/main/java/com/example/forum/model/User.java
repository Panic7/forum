package com.example.forum.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer ID;

    @NotBlank(message = "Enter the name")
    @Size(min = 4, max = 30, message = "Name must be of 4 to 30 symbols long")
    String username;

    String email;

    @NotBlank(message = "Password must be at least 5 characters long")
    String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Status status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference(value = "user-comment")
    List<Comment> comments;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "picture_id", referencedColumnName = "id")
    Picture picture;
}
