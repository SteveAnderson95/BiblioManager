package com.bibliomanager.service;

import com.bibliomanager.model.Category;
import com.bibliomanager.repository.CategoryRepository;

import java.util.List;

public class CategoryService {

    private CategoryRepository categoryRepo = new CategoryRepository();

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }
}
