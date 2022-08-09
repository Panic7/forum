package com.example.forum.repository;

import com.example.forum.model.Role;
import com.example.forum.model.Status;
import com.example.forum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.status = ?1 where u.id = ?2")
    void setUserStatusById(Status status, Integer userId);

    @Modifying
    @Query("update User u set u.role = ?1 where u.id = ?2")
    void setUserRoleById(Role role, Integer userId);

}
