package com.example.forum.converter;

import com.example.forum.model.Topic;
import com.example.forum.model.dto.CategoryDTO;
import com.example.forum.model.dto.CommentDTO;
import com.example.forum.model.dto.TopicDTO;
import com.example.forum.model.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TopicMapper {

    private final ModelMapper modelMapper;

    public Topic toEntity(TopicDTO topicDTO) {
        return modelMapper.map(topicDTO, Topic.class);
    }

    public TopicDTO toDTO(Topic topic) {
        TopicDTO topicDTO = modelMapper.map(topic, TopicDTO.class);
        topicDTO.setCategory(modelMapper.map(topic.getCategory(), CategoryDTO.class));
        topicDTO.setUser(modelMapper.map(topic.getUser(), UserDTO.class));
        topicDTO.setComments(topic.getComments().stream()
                .map(comment -> {
                    CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
                    commentDTO.setUserDTO(modelMapper.map(comment.getUser(), UserDTO.class));
                    return commentDTO;
                })
                .collect(Collectors.toList()));

        return topicDTO;
    }

    private List<Topic> toEntities(List<TopicDTO> topics) {
        return topics.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<TopicDTO> toDTOS(List<Topic> topics) {
        return topics.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
