package com.expensewise.service;

import com.expensewise.dto.request.ExpenseRequest;
import com.expensewise.dto.response.ExpenseResponse;
import com.expensewise.exception.ResourceNotFoundException;
import com.expensewise.model.Budget;
import com.expensewise.model.Category;
import com.expensewise.model.Expense;
import com.expensewise.repository.BudgetRepository;
import com.expensewise.repository.CategoryRepository;
import com.expensewise.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    /**
     * Adds a new expense and recalculates the associated budget if one exists.
     * Returns a warning message if the budget alert threshold is breached.
     */
    public ExpenseResponse add(String userId, ExpenseRequest request) {
        Category category = getOwnedCategory(userId, request.getCategoryId());

        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        Expense expense = Expense.builder()
                .userId(userId)
                .title(request.getTitle())
                .amount(request.getAmount())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .paymentMode(request.getPaymentMode())
                .note(request.getNote())
                .date(request.getDate())
                .month(date.getMonthValue())
                .year(date.getYear())
                .tags(request.getTags())
                .build();

        expense = expenseRepository.save(expense);
        return buildResponse(expense, recalculateBudget(userId, category.getId(), date.getMonthValue(), date.getYear(), category.getName()));
    }

    public Page<ExpenseResponse> getAll(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return expenseRepository.findByUserId(userId, pageable)
                .map(e -> buildResponse(e, null));
    }

    public ExpenseResponse getById(String userId, String id) {
        return buildResponse(findOwned(userId, id), null);
    }

    public List<ExpenseResponse> filterByMonthYear(String userId, int month, int year) {
        return expenseRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(e -> buildResponse(e, null))
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> filterByCategory(String userId, String categoryId) {
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(e -> buildResponse(e, null))
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> filterByPaymentMode(String userId, String paymentMode) {
        return expenseRepository.findByUserIdAndPaymentMode(userId, paymentMode).stream()
                .map(e -> buildResponse(e, null))
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> search(String userId, String keyword) {
        return expenseRepository.searchByKeyword(userId, keyword).stream()
                .map(e -> buildResponse(e, null))
                .collect(Collectors.toList());
    }

    /**
     * Updates the expense and recalculates the budget for the new month/year.
     * If the date changes, the old budget is also recalculated.
     */
    public ExpenseResponse update(String userId, String id, ExpenseRequest request) {
        Expense existing = findOwned(userId, id);
        Category category = getOwnedCategory(userId, request.getCategoryId());
        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        // Store old date info for budget recalculation if date changed
        int oldMonth = existing.getMonth();
        int oldYear = existing.getYear();
        String oldCategoryId = existing.getCategoryId();

        existing.setTitle(request.getTitle());
        existing.setAmount(request.getAmount());
        existing.setCategoryId(category.getId());
        existing.setCategoryName(category.getName());
        existing.setPaymentMode(request.getPaymentMode());
        existing.setNote(request.getNote());
        existing.setDate(request.getDate());
        existing.setMonth(date.getMonthValue());
        existing.setYear(date.getYear());
        existing.setTags(request.getTags());

        existing = expenseRepository.save(existing);

        // Recalculate old budget period if date changed
        if (oldMonth != date.getMonthValue() || oldYear != date.getYear()) {
            recalculateBudget(userId, oldCategoryId, oldMonth, oldYear, existing.getCategoryName());
        }

        String warning = recalculateBudget(userId, category.getId(), date.getMonthValue(), date.getYear(), category.getName());
        return buildResponse(existing, warning);
    }

    public void delete(String userId, String id) {
        Expense expense = findOwned(userId, id);
        expenseRepository.delete(expense);
        // Recalculate budget after deletion to update remaining amount
        recalculateBudget(userId, expense.getCategoryId(), expense.getMonth(), expense.getYear(), expense.getCategoryName());
    }

    /**
     * Recalculates spentAmount, remainingAmount, and isExceeded for a budget.
     * Returns a warning string if alert threshold is reached, null otherwise.
     */
    public String recalculateBudget(String userId, String categoryId, int month, int year, String categoryName) {
        Optional<Budget> budgetOpt = budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year);

        if (budgetOpt.isEmpty()) return null; // No budget set for this category/period

        Budget budget = budgetOpt.get();

        // Sum all expenses for this user, category, month, year
        double totalSpent = expenseRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        budget.setSpentAmount(totalSpent);
        budget.setRemainingAmount(budget.getBudgetAmount() - totalSpent);
        budget.setExceeded(totalSpent >= budget.getBudgetAmount());
        budgetRepository.save(budget);

        // Determine if warning should be triggered
        double usedPercent = (totalSpent / budget.getBudgetAmount()) * 100;
        if (usedPercent >= budget.getAlertThreshold()) {
            return String.format("⚠️ Warning: You have used %.1f%% of your budget for %s",
                    usedPercent, categoryName);
        }
        return null;
    }

    private Expense findOwned(String userId, String id) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
    }

    private Category getOwnedCategory(String userId, String categoryId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private ExpenseResponse buildResponse(Expense e, String budgetWarning) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .title(e.getTitle())
                .amount(e.getAmount())
                .categoryId(e.getCategoryId())
                .categoryName(e.getCategoryName())
                .paymentMode(e.getPaymentMode())
                .note(e.getNote())
                .date(e.getDate())
                .month(e.getMonth())
                .year(e.getYear())
                .tags(e.getTags())
                .createdAt(e.getCreatedAt())
                .budgetWarning(budgetWarning)
                .build();
    }
}
