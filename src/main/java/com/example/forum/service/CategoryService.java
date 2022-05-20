package com.example.forum.service;

import com.example.forum.model.Category;
import com.example.forum.model.dto.CategoryDTO;
import com.example.forum.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CategoryService {

    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    public CategoryDTO findByTitle(String title) {
        return toDTO(categoryRepository.findByTitle(title).orElseThrow());
    }

    public List<CategoryDTO> findAll() {
        return toDTOS((List<Category>) categoryRepository.findAll());
    }

    private Category toEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    private CategoryDTO toDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    private List<Category> toEntities(List<CategoryDTO> topics) {
        return topics.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private List<CategoryDTO> toDTOS(List<Category> topics) {
        return topics.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
