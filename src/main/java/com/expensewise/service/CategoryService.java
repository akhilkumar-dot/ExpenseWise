package com.expensewise.service;

import com.expensewise.dto.request.CategoryRequest;
import com.expensewise.dto.response.CategoryResponse;
import com.expensewise.exception.DuplicateResourceException;
import com.expensewise.exception.ResourceNotFoundException;
import com.expensewise.model.Category;
import com.expensewise.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Seeds 14 default categories (9 EXPENSE + 5 INCOME) for every new user.
     * Called automatically from AuthService on registration.
     */
    public void seedDefaultCategories(String userId) {
        // Default EXPENSE categories
        List<Category> defaults = List.of(
            buildDefault(userId, "Food",          "EXPENSE", "🍕", "#FF6B6B"),
            buildDefault(userId, "Transport",     "EXPENSE", "🚗", "#4ECDC4"),
            buildDefault(userId, "Rent",          "EXPENSE", "🏠", "#45B7D1"),
            buildDefault(userId, "Shopping",      "EXPENSE", "🛍",  "#96CEB4"),
            buildDefault(userId, "Entertainment", "EXPENSE", "🎬", "#FFEAA7"),
            buildDefault(userId, "Health",        "EXPENSE", "💊", "#DDA0DD"),
            buildDefault(userId, "Education",     "EXPENSE", "📚", "#98D8C8"),
            buildDefault(userId, "Utilities",     "EXPENSE", "💡", "#F7DC6F"),
            buildDefault(userId, "Other",         "EXPENSE", "📦", "#B0BEC5"),
            // Default INCOME categories
            buildDefault(userId, "Salary",        "INCOME",  "💰", "#27AE60"),
            buildDefault(userId, "Freelance",     "INCOME",  "💻", "#2980B9"),
            buildDefault(userId, "Investment",    "INCOME",  "📈", "#8E44AD"),
            buildDefault(userId, "Gift",          "INCOME",  "🎁", "#E74C3C"),
            buildDefault(userId, "Other",         "INCOME",  "💵", "#95A5A6")
        );
        categoryRepository.saveAll(defaults);
    }

    private Category buildDefault(String userId, String name, String type, String icon, String color) {
        return Category.builder()
                .userId(userId)
                .name(name)
                .type(type)
                .icon(icon)
                .colorCode(color)
                .isDefault(true)
                .build();
    }

    public CategoryResponse create(String userId, CategoryRequest request) {
        if (categoryRepository.existsByUserIdAndNameAndType(userId, request.getName(), request.getType())) {
            throw new DuplicateResourceException(
                    "Category '" + request.getName() + "' of type " + request.getType() + " already exists");
        }
        Category category = Category.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType().toUpperCase())
                .icon(request.getIcon())
                .colorCode(request.getColorCode())
                .isDefault(false)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll(String userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getById(String userId, String id) {
        return toResponse(findOwned(userId, id));
    }

    public List<CategoryResponse> getByType(String userId, String type) {
        return categoryRepository.findByUserIdAndType(userId, type.toUpperCase()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse update(String userId, String id, CategoryRequest request) {
        Category category = findOwned(userId, id);
        if (request.getName() != null) category.setName(request.getName());
        if (request.getType() != null) category.setType(request.getType().toUpperCase());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getColorCode() != null) category.setColorCode(request.getColorCode());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(String userId, String id) {
        Category category = findOwned(userId, id);
        categoryRepository.delete(category);
    }

    /** Ensures the category belongs to the requesting user */
    private Category findOwned(String userId, String id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .name(c.getName())
                .type(c.getType())
                .icon(c.getIcon())
                .colorCode(c.getColorCode())
                .isDefault(c.isDefault())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
