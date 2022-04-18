package com.example.forum.service;

import com.example.forum.model.Category;
import com.example.forum.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CategoryService {

    CategoryRepository categoryRepository;

    public Category findByTitle(String title) {
        return categoryRepository.findByTitle(title).orElseThrow();
    }
}
