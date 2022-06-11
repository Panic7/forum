package com.example.forum.service;

import com.example.forum.converter.CommentMapper;
import com.example.forum.model.Comment;
import com.example.forum.model.dto.CommentDTO;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CommentService {
    CommentMapper commentMapper;
    CommentRepository commentRepository;
    UserRepository userRepository;
    DateService dateService;

    public CommentDTO findById(Integer id) {
        return commentMapper.toDTO(commentRepository.findById(id).orElseThrow());
    }

    public List<CommentDTO> findAll() {
        return commentMapper.toDTOS((List<Comment>) commentRepository.findAll());
    }

    public void save(CommentDTO commentDTO, Integer userId) {
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setCreationDate(LocalDateTime.now());
        comment.setUser(userRepository.findById(userId).orElseThrow());

        commentRepository.save(comment);
    }

    public List<CommentDTO> actualizeSinceCreation(List<CommentDTO> commentDTO) {
        commentDTO.forEach(c -> c.setSinceCreation(
                dateService.actualizeSinceCreation(c.getCreationDate())));
        return commentDTO;
    }

    public CommentDTO actualizeSinceCreation(CommentDTO commentDTO) {
        commentDTO.setSinceCreation(dateService.actualizeSinceCreation(commentDTO.getCreationDate()));
        return commentDTO;
    }

}
