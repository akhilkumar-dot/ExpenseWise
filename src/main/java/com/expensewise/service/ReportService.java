package com.expensewise.service;

import com.expensewise.dto.response.ReportResponse;
import com.expensewise.model.Budget;
import com.expensewise.model.Expense;
import com.expensewise.model.Income;
import com.expensewise.repository.BudgetRepository;
import com.expensewise.repository.ExpenseRepository;
import com.expensewise.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Report service — all analytics and aggregation logic lives here.
 * Works exclusively on data owned by the requesting userId.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final BudgetRepository budgetRepository;

    /**
     * Monthly summary: income, expense, savings, top category, budget status.
     */
    public ReportResponse.MonthlySummary getMonthlySummary(String userId, int month, int year) {
        List<Expense> expenses = expenseRepository.findByUserIdAndMonthAndYear(userId, month, year);
        List<Income> incomes = incomeRepository.findByUserIdAndMonthAndYear(userId, month, year);
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);

        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
        double netSavings = totalIncome - totalExpense;
        double savingsPercentage = totalIncome > 0 ? (netSavings / totalIncome) * 100 : 0;

        // Top spending category: group expenses by category, find max
        String topCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategoryName, Collectors.summingDouble(Expense::getAmount)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Budget status for each set budget this month
        List<ReportResponse.BudgetStatus> budgetStatusList = budgets.stream()
                .map(b -> {
                    String status;
                    if (b.isExceeded()) {
                        status = "EXCEEDED";
                    } else if (b.getSpentAmount() >= (b.getAlertThreshold() / 100.0) * b.getBudgetAmount()) {
                        status = "WARNING";
                    } else {
                        status = "ON_TRACK";
                    }
                    return ReportResponse.BudgetStatus.builder()
                            .categoryName(b.getCategoryName())
                            .budgetAmount(b.getBudgetAmount())
                            .spentAmount(b.getSpentAmount())
                            .status(status)
                            .build();
                }).collect(Collectors.toList());

        return ReportResponse.MonthlySummary.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .savingsPercentage(Math.round(savingsPercentage * 100.0) / 100.0)
                .topSpendingCategory(topCategory)
                .budgetStatus(budgetStatusList)
                .build();
    }

    /**
     * Yearly summary: aggregates 12 months to find best/worst periods.
     */
    public ReportResponse.YearlySummary getYearlySummary(String userId, int year) {
        List<Expense> expenses = expenseRepository.findByUserIdAndYear(userId, year);
        List<Income> incomes = incomeRepository.findByUserIdAndYear(userId, year);

        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double netSavings = totalIncome - totalExpense;

        // Group expenses by month to find worst spending month
        Map<Integer, Double> expenseByMonth = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getMonth, Collectors.summingDouble(Expense::getAmount)));

        // Group incomes by month to compute savings per month
        Map<Integer, Double> incomeByMonth = incomes.stream()
                .collect(Collectors.groupingBy(Income::getMonth, Collectors.summingDouble(Income::getAmount)));

        // Best savings month: highest (income - expense)
        String bestSavingsMonth = "N/A";
        String worstSpendingMonth = "N/A";

        if (!expenseByMonth.isEmpty()) {
            int worst = expenseByMonth.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).get().getKey();
            worstSpendingMonth = Month.of(worst).name();
        }

        // Find month with highest net savings
        Set<Integer> allMonths = new HashSet<>();
        allMonths.addAll(expenseByMonth.keySet());
        allMonths.addAll(incomeByMonth.keySet());

        if (!allMonths.isEmpty()) {
            int best = allMonths.stream()
                    .max(Comparator.comparingDouble(m ->
                            incomeByMonth.getOrDefault(m, 0.0) - expenseByMonth.getOrDefault(m, 0.0)))
                    .orElse(1);
            bestSavingsMonth = Month.of(best).name();
        }

        double averageMonthlyExpense = expenseByMonth.isEmpty() ? 0 :
                expenseByMonth.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);

        return ReportResponse.YearlySummary.builder()
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .bestSavingsMonth(bestSavingsMonth)
                .worstSpendingMonth(worstSpendingMonth)
                .averageMonthlyExpense(Math.round(averageMonthlyExpense * 100.0) / 100.0)
                .build();
    }

    /**
     * Category breakdown: sorted by totalSpent descending, with percentage of total.
     */
    public List<ReportResponse.CategoryBreakdown> getCategoryBreakdown(String userId, int month, int year) {
        List<Expense> expenses = expenseRepository.findByUserIdAndMonthAndYear(userId, month, year);
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategoryName, Collectors.summingDouble(Expense::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> {
                    double pct = totalExpense > 0 ? (e.getValue() / totalExpense) * 100 : 0;
                    return ReportResponse.CategoryBreakdown.builder()
                            .categoryName(e.getKey())
                            .totalSpent(e.getValue())
                            .percentage(Math.round(pct * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Income vs expense for a given month/year.
     */
    public ReportResponse.IncomeVsExpense getIncomeVsExpense(String userId, int month, int year) {
        double totalExpense = expenseRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream().mapToDouble(Expense::getAmount).sum();
        double totalIncome = incomeRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream().mapToDouble(Income::getAmount).sum();

        return ReportResponse.IncomeVsExpense.builder()
                .month(month).year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(totalIncome - totalExpense)
                .build();
    }

    /**
     * Top N expenses for a month sorted by amount descending.
     */
    public List<Expense> getTopExpenses(String userId, int month, int year, int limit) {
        return expenseRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Month-by-month savings trend for the full year.
     */
    public List<ReportResponse.SavingsTrend> getSavingsTrend(String userId, int year) {
        List<Expense> expenses = expenseRepository.findByUserIdAndYear(userId, year);
        List<Income> incomes = incomeRepository.findByUserIdAndYear(userId, year);

        Map<Integer, Double> expenseByMonth = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getMonth, Collectors.summingDouble(Expense::getAmount)));
        Map<Integer, Double> incomeByMonth = incomes.stream()
                .collect(Collectors.groupingBy(Income::getMonth, Collectors.summingDouble(Income::getAmount)));

        // Return all 12 months even if there's no data (shows zeros for inactive months)
        List<ReportResponse.SavingsTrend> trend = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            double inc = incomeByMonth.getOrDefault(m, 0.0);
            double exp = expenseByMonth.getOrDefault(m, 0.0);
            trend.add(ReportResponse.SavingsTrend.builder()
                    .month(m)
                    .monthName(Month.of(m).name())
                    .totalIncome(inc)
                    .totalExpense(exp)
                    .netSavings(inc - exp)
                    .build());
        }
        return trend;
    }

    /**
     * Payment mode breakdown for a month.
     */
    public List<ReportResponse.PaymentModeBreakdown> getPaymentModeBreakdown(String userId, int month, int year) {
        List<Expense> expenses = expenseRepository.findByUserIdAndMonthAndYear(userId, month, year);
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getPaymentMode, Collectors.summingDouble(Expense::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> {
                    double pct = total > 0 ? (e.getValue() / total) * 100 : 0;
                    return ReportResponse.PaymentModeBreakdown.builder()
                            .paymentMode(e.getKey())
                            .totalSpent(e.getValue())
                            .percentage(Math.round(pct * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
