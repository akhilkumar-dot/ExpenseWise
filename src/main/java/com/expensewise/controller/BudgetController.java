package com.expensewise.controller;

import com.expensewise.dto.request.BudgetRequest;
import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.BudgetResponse;
import com.expensewise.service.BudgetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> create(
            HttpServletRequest request,
            @Valid @RequestBody BudgetRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget set", budgetService.create(userId, body)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAll(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Budgets fetched", budgetService.getAll(userId)));
    }

    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getByMonth(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Budgets for month fetched",
                budgetService.getByMonth(userId, month, year)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getById(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Budget fetched", budgetService.getById(userId, id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid @RequestBody BudgetRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Budget updated", budgetService.update(userId, id, body)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        budgetService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted", null));
    }
}
