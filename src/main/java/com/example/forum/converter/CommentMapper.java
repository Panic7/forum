package com.example.forum.converter;

import com.example.forum.model.Comment;
import com.example.forum.model.Topic;
import com.example.forum.model.dto.CommentDTO;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    public CommentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        Converter<Topic, Integer> topicToInt = new AbstractConverter<>() {
            @Override
            protected Integer convert(Topic source) {
                return null == source ? null : source.getId();
            }
        };
        Converter<Integer, Topic> intToTopic = new AbstractConverter<>() {
            @Override
            protected Topic convert(Integer source) {
                Topic topic = new Topic();
                topic.setId(source);
                return null == source ? null : topic;
            }
        };
        modelMapper.addConverter(topicToInt);
        modelMapper.addConverter(intToTopic);
    }

    public Comment toEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    public CommentDTO toDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    public List<Comment> toEntities(List<CommentDTO> comments) {
        return comments.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> toDTOS(List<Comment> comments) {
        return comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
