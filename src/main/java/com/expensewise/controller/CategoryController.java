package com.expensewise.controller;

import com.expensewise.dto.request.CategoryRequest;
import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.CategoryResponse;
import com.expensewise.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            HttpServletRequest request,
            @Valid @RequestBody CategoryRequest body) {
        String userId = (String) request.getAttribute("userId");
        CategoryResponse response = categoryService.create(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryService.getAll(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Category fetched", categoryService.getById(userId, id)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getByType(
            HttpServletRequest request, @PathVariable String type) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryService.getByType(userId, type)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.update(userId, id, body)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        categoryService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }
}
