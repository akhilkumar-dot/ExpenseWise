package com.expensewise.service;

import com.expensewise.dto.request.IncomeRequest;
import com.expensewise.dto.response.IncomeResponse;
import com.expensewise.exception.ResourceNotFoundException;
import com.expensewise.model.Category;
import com.expensewise.model.Income;
import com.expensewise.repository.CategoryRepository;
import com.expensewise.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    public IncomeResponse add(String userId, IncomeRequest request) {
        Category category = getOwnedCategory(userId, request.getCategoryId());
        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        Income income = Income.builder()
                .userId(userId)
                .title(request.getTitle())
                .amount(request.getAmount())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .source(request.getSource())
                .note(request.getNote())
                .date(request.getDate())
                .month(date.getMonthValue())
                .year(date.getYear())
                .build();

        return toResponse(incomeRepository.save(income));
    }

    public Page<IncomeResponse> getAll(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return incomeRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    public IncomeResponse getById(String userId, String id) {
        return toResponse(findOwned(userId, id));
    }

    public List<IncomeResponse> filterByMonthYear(String userId, int month, int year) {
        return incomeRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<IncomeResponse> filterBySource(String userId, String source) {
        return incomeRepository.findByUserIdAndSource(userId, source.toUpperCase()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public IncomeResponse update(String userId, String id, IncomeRequest request) {
        Income income = findOwned(userId, id);
        Category category = getOwnedCategory(userId, request.getCategoryId());
        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        income.setCategoryId(category.getId());
        income.setCategoryName(category.getName());
        income.setSource(request.getSource());
        income.setNote(request.getNote());
        income.setDate(request.getDate());
        income.setMonth(date.getMonthValue());
        income.setYear(date.getYear());

        return toResponse(incomeRepository.save(income));
    }

    public void delete(String userId, String id) {
        incomeRepository.delete(findOwned(userId, id));
    }

    private Income findOwned(String userId, String id) {
        return incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found: " + id));
    }

    private Category getOwnedCategory(String userId, String categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private IncomeResponse toResponse(Income i) {
        return IncomeResponse.builder()
                .id(i.getId())
                .userId(i.getUserId())
                .title(i.getTitle())
                .amount(i.getAmount())
                .categoryId(i.getCategoryId())
                .categoryName(i.getCategoryName())
                .source(i.getSource())
                .note(i.getNote())
                .date(i.getDate())
                .month(i.getMonth())
                .year(i.getYear())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
