package com.example.forum.service;

import com.example.forum.model.Category;
import com.example.forum.model.Topic;
import com.example.forum.model.dto.CategoryDTO;
import com.example.forum.model.dto.CommentDTO;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.model.dto.UserDTO;
import com.example.forum.repository.CategoryRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
        return toDTOS(topicRepository.findAll());
    }

    public TopicDTO findById(Integer id) {
        return toDTO(topicRepository.findById(id).orElseThrow());
    }

    @Transactional
    public boolean save(TopicDTO topicDTO, Integer userId) {

        Topic topic = toEntity(topicDTO);
        topic.setCreationDate(LocalDateTime.now());
        topic.setUser(userRepository.getById(userId));
        Optional<Category> optional = categoryRepository.findByTitle(topicDTO.getCategoryDTO().getTitle());
        if (optional.isEmpty()) {
            return false;
        }
        topic.setCategory(optional.get());
        if (topic.getViews() == null) topic.setViews(0);
        topicRepository.save(topic);
        return true;
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

    public Topic toEntity(TopicDTO topicDTO) {
        return modelMapper.map(topicDTO, Topic.class);
    }

    public TopicDTO toDTO(Topic topic) {
        TopicDTO topicDTO = modelMapper.map(topic, TopicDTO.class);
        topicDTO.setCategoryDTO(modelMapper.map(topic.getCategory(), CategoryDTO.class));
        topicDTO.setUserDTO(modelMapper.map(topic.getUser(), UserDTO.class));
        List<CommentDTO> commentDTOS = topic.getComments().stream()
                .map(cmnt -> modelMapper.map(cmnt, CommentDTO.class))
                .toList();
        topicDTO.setComments(commentDTOS);
        return topicDTO;
    }

    private List<Topic> toEntities(List<TopicDTO> topics) {
        return topics.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private List<TopicDTO> toDTOS(List<Topic> topics) {
        return topics.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TopicDTO> actualizeSinceCreation(List<TopicDTO> topicDTOS) {
        return topicDTOS.stream()
                .map(this::actualizeSinceCreation)
                .collect(Collectors.toList());
    }

    public TopicDTO actualizeSinceCreation(TopicDTO topicDTO) {
        LocalDateTime startDateTime = topicDTO.getCreationDate();
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime tempDateTime = LocalDateTime.from(startDateTime);

        long years = tempDateTime.until( endDateTime, ChronoUnit.YEARS );
        tempDateTime = tempDateTime.plusYears( years );

        long months = tempDateTime.until( endDateTime, ChronoUnit.MONTHS );
        tempDateTime = tempDateTime.plusMonths( months );

        long days = tempDateTime.until( endDateTime, ChronoUnit.DAYS );
        tempDateTime = tempDateTime.plusDays( days );

        long hours = tempDateTime.until( endDateTime, ChronoUnit.HOURS );
        tempDateTime = tempDateTime.plusHours( hours );

        long minutes = tempDateTime.until( endDateTime, ChronoUnit.MINUTES );
        tempDateTime = tempDateTime.plusMinutes( minutes );

        if(years > 0) {
            if(months > 0 ) {
                topicDTO.setSinceCreation(years + " years " + months + " months ago");
                return topicDTO;
            }
            topicDTO.setSinceCreation(years + " years ago");
            return topicDTO;
        }
        if(months > 0) {
            topicDTO.setSinceCreation(months + " months ago");
            return topicDTO;
        }
        if(days > 0) {
            topicDTO.setSinceCreation(days + " days ago");
            return topicDTO;
        }
        if(hours > 0) {
            topicDTO.setSinceCreation(hours + " hours ago");
            return topicDTO;
        }
        if(minutes > 0) {
            topicDTO.setSinceCreation(minutes + " minutes ago");
            return topicDTO;
        }
        topicDTO.setSinceCreation("just now");
        return topicDTO;
    }
}
