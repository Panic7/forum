package com.example.forum.repository;

import com.example.forum.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {
    Optional<Comment> findById(Integer id);

}
