package com.example.forum.service;

import com.example.forum.converter.TopicMapper;
import com.example.forum.model.Category;
import com.example.forum.model.Topic;
import com.example.forum.model.TopicMark;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.repository.CategoryRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class TopicService {

    @NonFinal
    @Value("${PRODUCTS_PER_PAGE}")
    int PRODUCTS_PER_PAGE;
    CategoryRepository categoryRepository;
    TopicRepository topicRepository;
    UserRepository userRepository;
    TopicMapper topicMapper;
    TopicMarkService topicMarkService;
    DateService dateService;

    public Page<TopicDTO> findPage(int currentPage, String sortParam) {
        Pageable pageable = PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE, Sort.by(sortParam).descending());
        return topicRepository.findAll(pageable).map(topicMapper::toDTO);
    }

    public Page<TopicDTO> findPage(int currentPage, String sortParam, Integer categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        Pageable pageable = PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE, Sort.by(sortParam).descending());
        return topicRepository.findAllByCategory(pageable, category).map(topicMapper::toDTO);
    }

    public Page<TopicDTO> findPage(int currentPage, String sortParam, String key) {
        Pageable pageable = PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE, Sort.by(sortParam).descending());
        return topicRepository.findAllByHeaderContaining(pageable, key).map(topicMapper::toDTO);
    }

    public TopicDTO findById(Integer id) {
        return topicMapper.toDTO(topicRepository.findById(id).orElseThrow());
    }

    public void deleteById(Integer id) {
        topicRepository.deleteById(id);
    }

    @Transactional
    public TopicDTO findByIdEagerly(Integer id) {
        Topic topic = topicRepository.findByIdAndFetchCommentsEagerly(id);
        return topicMapper.toDTO(topic);
    }

    public boolean save(TopicDTO topicDTO, Integer userId) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setCreationDate(LocalDateTime.now());
        topic.setUser(userRepository.getById(userId));
        Optional<Category> optional = categoryRepository.findByTitle(topicDTO.getCategory().getTitle());
        if (optional.isEmpty()) {
            return false;
        }
        if (topicDTO.getScore() == null) {
            topic.setScore(0d);
        } else {
            topic.setScore(topicDTO.getScore());
        }
        topic.setCategory(optional.get());
        topicRepository.save(topic);
        return true;
    }


    @Transactional
    public void update(TopicDTO topicDTO, Integer id) {
        Topic topicForSearch = Topic.builder().id(id).build();
        Topic findedTopic = topicRepository.findById(id).orElseThrow();
        findedTopic.setHeader(topicDTO.getHeader());
        findedTopic.setDescription(topicDTO.getDescription());
        findedTopic.setAnonymous(topicDTO.isAnonymous());
        findedTopic.getCategory().setTitle(topicDTO.getCategory().getTitle());
    }

    @Transactional
    public void delete(Integer id) {
        topicRepository.deleteById(id);
    }

    public TopicDTO actualizeDataDTO(TopicDTO topicDTO, Integer currentUserId) {
        topicDTO.setLikes(topicMarkService.getCountPositiveMarks(topicDTO.getId()));
        topicDTO.setDislikes(topicMarkService.getCountNegativeMarks(topicDTO.getId()));
        Optional<TopicMark> optional = topicMarkService.findByTopicAndUser(topicDTO.getId(), currentUserId);
        optional.ifPresent(topicMark -> topicDTO.setMark(topicMark.getMark()));
        topicDTO.setSinceCreation(dateService.actualizeSinceCreation(topicDTO.getCreationDate()));
        topicDTO.getComments().forEach(c -> c.setSinceCreation(
                dateService.actualizeSinceCreation(c.getCreationDate())));

        return topicDTO;
    }

    public List<TopicDTO> actualizeSinceCreation(List<TopicDTO> topicDTOS) {
        topicDTOS.forEach(this::actualizeSinceCreation);
        return topicDTOS;
    }

    public TopicDTO actualizeSinceCreation(TopicDTO topicDTO) {
        topicDTO.setSinceCreation(dateService.actualizeSinceCreation(topicDTO.getCreationDate()));
        topicDTO.getComments().forEach(c -> c.setSinceCreation(
                dateService.actualizeSinceCreation(c.getCreationDate())));
        return topicDTO;
    }

}
