package com.bibliomanager.service;

import com.bibliomanager.model.Category;
import com.bibliomanager.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private CategoryRepository categoryRepo = new CategoryRepository();

    public void addCategory(Category cat) {
        try {
            categoryRepo.insert(cat);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                throw new RuntimeException("Category already exists.");
            throw new RuntimeException("Error adding category", e);
        }
    }

    public void updateCategory(Category cat) {
        try {
            categoryRepo.update(cat);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    public void deleteCategory(long id) {
        try {
            categoryRepo.delete(id);
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("Cannot delete: category has books.");
            throw new RuntimeException("Error deleting category", e);
        }
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }
}
