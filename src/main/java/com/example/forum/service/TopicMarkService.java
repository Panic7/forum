package com.example.forum.service;


import com.example.forum.model.Topic;
import com.example.forum.model.TopicMark;
import com.example.forum.model.TopicUserId;
import com.example.forum.model.User;
import com.example.forum.repository.TopicMarkRepository;
import com.example.forum.repository.TopicRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class TopicMarkService {

    TopicMarkRepository topicMarkRepository;
    TopicRepository topicRepository;

    public Set<TopicMark> findAll() {
        Set<TopicMark> set = new HashSet<>();
        for (TopicMark topicMark : topicMarkRepository.findAll()) {
            set.add(topicMark);
        }
        return set;
    }

    public Integer getCountPositiveMarks(Integer topicId) {
        return topicMarkRepository.countByPk_TopicAndMark(Topic.builder().id(topicId).build(), true);
    }

    public Integer getCountNegativeMarks(Integer topicId) {
        return topicMarkRepository.countByPk_TopicAndMark(Topic.builder().id(topicId).build(), false);
    }

    public Optional<TopicMark> findByTopicAndUser(Integer topicId, Integer userId) {
        User user = User.builder().id(userId).build();
        Topic topic = Topic.builder().id(topicId).build();
        TopicUserId topicUserId = TopicUserId.builder().topic(topic).user(user).build();
        return topicMarkRepository.findByPk(topicUserId);
    }

    @Transactional
    public void saveMark(Boolean mark, Integer userId, Integer topicId) {
        Optional<TopicMark> searched = findByTopicAndUser(topicId, userId);
        if (searched.isPresent()) {
            TopicMark topicMark = searched.get();
            if (topicMark.getMark() == mark) {
                topicMark.setMark(null);
            } else {
                topicMark.setMark(mark);
            }
        } else {
            User user = User.builder().id(userId).build();
            Topic topic = Topic.builder().id(topicId).build();
            TopicMark topicMark = new TopicMark();
            topicMark.setMark(mark);
            topicMark.setPk(TopicUserId.builder().user(user).topic(topic).build());
            topicMarkRepository.save(topicMark);
        }
        Double score = calculateScore(getCountPositiveMarks(topicId), getCountNegativeMarks(topicId));
        topicRepository.findById(topicId).get().setScore(score);
    }

    @Transactional
    public void saveMark(TopicMark topicMark) {
        Optional<TopicMark> searched = findByTopicAndUser(
                topicMark.getPk().getTopic().getId(),
                topicMark.getPk().getUser().getId());
        if (searched.isPresent()) {
            TopicMark searchedTopicMark = searched.get();
            if (searchedTopicMark.getMark() == topicMark.getMark()) {
                searchedTopicMark.setMark(!topicMark.getMark());
            } else {
                searchedTopicMark.setMark(topicMark.getMark());
            }
        } else {
            topicMarkRepository.save(topicMark);
        }
    }

    private double calculateScore(int up, int down) {
        if (up == 0) return -down;
        int n = up + down;
        double z = 1.64485;
        double phat = (double) up / n;
        return Math.sqrt((phat + z * z / (2 * n) - z * (phat * (1 - phat) + z * z / (4 * n)) / n)) / (1 + z * z / n);
    }
}
