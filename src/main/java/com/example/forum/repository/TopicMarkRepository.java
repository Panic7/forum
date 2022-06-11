package com.example.forum.repository;

import com.example.forum.model.Topic;
import com.example.forum.model.TopicMark;
import com.example.forum.model.TopicUserId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicMarkRepository extends CrudRepository<TopicMark, Integer> {

    Optional<TopicMark> findByPk(TopicUserId topicUserId);

    Integer countByPk_TopicAndMark(Topic topic, Boolean mark);
}
