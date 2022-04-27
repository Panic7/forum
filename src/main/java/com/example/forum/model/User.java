package com.example.forum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer ID;

    //@NotBlank(message = "Enter the name")
    //@Size(min = 4, max = 30, message = "Name must be of 4 to 30 symbols long ")
    String name;

    String email;

    //@NotBlank(message = "Password must be at least 6 characters long")
    String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Status status;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "picture_id", referencedColumnName = "id")
    Picture picture;
}
