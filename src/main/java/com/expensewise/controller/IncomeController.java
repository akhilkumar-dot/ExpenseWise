package com.expensewise.controller;

import com.expensewise.dto.request.IncomeRequest;
import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.IncomeResponse;
import com.expensewise.service.IncomeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> add(
            HttpServletRequest request,
            @Valid @RequestBody IncomeRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Income added", incomeService.add(userId, body)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<IncomeResponse>>> getAll(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Incomes fetched", incomeService.getAll(userId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> getById(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Income fetched", incomeService.getById(userId, id)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<?>> filter(
            HttpServletRequest request,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String source) {
        String userId = (String) request.getAttribute("userId");

        if (month != null && year != null) {
            List<IncomeResponse> result = incomeService.filterByMonthYear(userId, month, year);
            return ResponseEntity.ok(ApiResponse.success("Filtered by month/year", result));
        }
        if (source != null) {
            List<IncomeResponse> result = incomeService.filterBySource(userId, source);
            return ResponseEntity.ok(ApiResponse.success("Filtered by source", result));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Provide filter: month+year or source"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @Valid @RequestBody IncomeRequest body) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Income updated", incomeService.update(userId, id, body)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            HttpServletRequest request, @PathVariable String id) {
        String userId = (String) request.getAttribute("userId");
        incomeService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Income deleted", null));
    }
}
