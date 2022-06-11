package com.example.forum.repository;

import com.example.forum.model.Category;
import com.example.forum.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Query("SELECT t FROM Topic t LEFT JOIN t.comments WHERE t.id = (:id)")
    Topic findByIdAndFetchCommentsEagerly(@Param("id") Integer id);

    Page<Topic> findAllByCategory(Pageable pageable, Category category);

}
