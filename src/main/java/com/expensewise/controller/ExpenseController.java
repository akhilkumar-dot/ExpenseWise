package com.expensewise.controller;

import com.expensewise.dto.request.ExpenseRequest;
import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.ExpenseResponse;
import com.expensewise.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> add(
            HttpServletRequest request,
            @Valid @RequestBody ExpenseRequest body) {
        String userId = (String) request.getAttribute("userId");
        ExpenseResponse response = expenseService.add(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense added", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getAll(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched", expenseService.getAll(userId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Expense fetched", expenseService.getById(userId, id)));
    }

    /**
     * Unified filter endpoint — handles month+year, categoryId, and paymentMode filters.
     * At least one filter param must be provided.
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<?>> filter(
            HttpServletRequest request,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String paymentMode) {
        String userId = (String) request.getAttribute("userId");

        if (month != null && year != null) {
            return ResponseEntity.ok(ApiResponse.success("Filtered by month/year",
                    expenseService.filterByMonthYear(userId, month, year)));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(ApiResponse.success("Filtered by category",
                    expenseService.filterByCategory(userId, categoryId)));
        }
        if (paymentMode != null) {
            return ResponseEntity.ok(ApiResponse.success("Filtered by payment mode",
                    expenseService.filterByPaymentMode(userId, paymentMode)));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Provide at least one filter: month+year, categoryId, or paymentMode"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> search(
            HttpServletRequest request,
            @RequestParam String keyword) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Search results", expenseService.search(userId, keyword)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Expense updated", expenseService.update(userId, id, body)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        expenseService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted", null));
    }
}
