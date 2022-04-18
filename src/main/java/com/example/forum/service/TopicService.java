package com.example.forum.service;

import com.example.forum.model.Category;
import com.example.forum.model.Topic;
import com.example.forum.model.User;
import com.example.forum.model.dto.CategoryDTO;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.repository.CategoryRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class TopicService {

    CategoryRepository categoryRepository;
    TopicRepository topicRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;

    public List<TopicDTO> findAll() {
        return toDTOList(topicRepository.findAll());
    }

    public TopicDTO findById(Integer id) {
        return toDTO(topicRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void save(TopicDTO topicDTO) {
        Topic topic = toEntity(topicDTO);

        topic.setCategory(categoryRepository.findByTitle(topicDTO.getCategoryDTO().getTitle()).orElseThrow());
        topic.setAuthor(userRepository.findByName(topicDTO.getUserDTO().getName()).orElseThrow());

        topicRepository.save(topic);
    }


    @Transactional
    public void update(TopicDTO topicDTO, Integer id) {
        Topic findedTopic = topicRepository.findById(id).orElseThrow();

        findedTopic.setHeader(topicDTO.getHeader());
        findedTopic.setDescription(topicDTO.getDescription());
        findedTopic.setAnonymous(topicDTO.isAnonymous());
        findedTopic.getCategory().setTitle(topicDTO.getCategoryDTO().getTitle());
    }

    @Transactional
    public void delete(Integer id) {
        topicRepository.deleteById(id);
    }

    private TopicDTO toDTO(Topic topic) {
        TopicDTO topicDTO = modelMapper.map(topic, TopicDTO.class);

        topicDTO.setCategoryDTO(modelMapper.map(topic.getCategory(), CategoryDTO.class));
        topicDTO.setUserDTO(modelMapper.map(topic.getAuthor(), UserDTO.class));

        return topicDTO;
    }

    private Topic toEntity(TopicDTO topicDTO) {
        Topic topic = modelMapper.map(topicDTO, Topic.class);

        topic.setAuthor(modelMapper.map(topicDTO.getUserDTO(), User.class));
        topic.setCategory(modelMapper.map(topicDTO.getCategoryDTO(), Category.class));

        return topic;
    }

    private List<Topic> toEntityList(List<TopicDTO> topics) {
        return topics.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private List<TopicDTO> toDTOList(List<Topic> topics) {
        return topics.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
