package com.expensewise.service;

import com.expensewise.dto.request.BudgetRequest;
import com.expensewise.dto.response.BudgetResponse;
import com.expensewise.exception.DuplicateResourceException;
import com.expensewise.exception.ResourceNotFoundException;
import com.expensewise.model.Budget;
import com.expensewise.model.Category;
import com.expensewise.model.Expense;
import com.expensewise.repository.BudgetRepository;
import com.expensewise.repository.CategoryRepository;
import com.expensewise.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Creates a new budget. Calculates spentAmount from existing expenses
     * so a newly set budget immediately reflects the current month's spending.
     */
    public BudgetResponse create(String userId, BudgetRequest request) {
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear())) {
            throw new DuplicateResourceException(
                    "Budget already exists for this category in " + request.getMonth() + "/" + request.getYear());
        }

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Compute how much has already been spent in this period
        double alreadySpent = expenseRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, request.getCategoryId(), request.getMonth(), request.getYear())
                .stream().mapToDouble(Expense::getAmount).sum();

        Budget budget = Budget.builder()
                .userId(userId)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .budgetAmount(request.getBudgetAmount())
                .spentAmount(alreadySpent)
                .remainingAmount(request.getBudgetAmount() - alreadySpent)
                .month(request.getMonth())
                .year(request.getYear())
                .alertThreshold(request.getAlertThreshold())
                .isExceeded(alreadySpent >= request.getBudgetAmount())
                .build();

        return toResponse(budgetRepository.save(budget));
    }

    public List<BudgetResponse> getAll(String userId) {
        return budgetRepository.findByUserId(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<BudgetResponse> getByMonth(String userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public BudgetResponse getById(String userId, String id) {
        return toResponse(findOwned(userId, id));
    }

    public BudgetResponse update(String userId, String id, BudgetRequest request) {
        Budget budget = findOwned(userId, id);
        budget.setBudgetAmount(request.getBudgetAmount());
        budget.setAlertThreshold(request.getAlertThreshold());
        // Recalculate remaining after amount change
        budget.setRemainingAmount(request.getBudgetAmount() - budget.getSpentAmount());
        budget.setExceeded(budget.getSpentAmount() >= request.getBudgetAmount());
        return toResponse(budgetRepository.save(budget));
    }

    public void delete(String userId, String id) {
        budgetRepository.delete(findOwned(userId, id));
    }

    private Budget findOwned(String userId, String id) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found: " + id));
    }

    private BudgetResponse toResponse(Budget b) {
        return BudgetResponse.builder()
                .id(b.getId())
                .userId(b.getUserId())
                .categoryId(b.getCategoryId())
                .categoryName(b.getCategoryName())
                .budgetAmount(b.getBudgetAmount())
                .spentAmount(b.getSpentAmount())
                .remainingAmount(b.getRemainingAmount())
                .month(b.getMonth())
                .year(b.getYear())
                .alertThreshold(b.getAlertThreshold())
                .isExceeded(b.isExceeded())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
