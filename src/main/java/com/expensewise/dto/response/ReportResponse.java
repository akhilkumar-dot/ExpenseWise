package com.expensewise.dto.response;

import lombok.*;

import java.util.List;

/**
 * Aggregated response objects for the Reports API.
 * Inner classes represent individual report structures.
 */
public class ReportResponse {

    /** Monthly summary report */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySummary {
        private int month;
        private int year;
        private double totalIncome;
        private double totalExpense;
        private double netSavings;
        private double savingsPercentage;
        private String topSpendingCategory;
        private List<BudgetStatus> budgetStatus;
    }

    /** Per-budget line in the monthly summary */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetStatus {
        private String categoryName;
        private double budgetAmount;
        private double spentAmount;
        /** ON_TRACK / WARNING / EXCEEDED */
        private String status;
    }

    /** Category breakdown item */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private String categoryName;
        private double totalSpent;
        private double percentage;
    }

    /** Month-by-month savings trend item */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavingsTrend {
        private int month;
        private String monthName;
        private double totalIncome;
        private double totalExpense;
        private double netSavings;
    }

    /** Yearly summary report */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YearlySummary {
        private int year;
        private double totalIncome;
        private double totalExpense;
        private double netSavings;
        private String bestSavingsMonth;
        private String worstSpendingMonth;
        private double averageMonthlyExpense;
    }

    /** Income vs expense comparison */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncomeVsExpense {
        private int month;
        private int year;
        private double totalIncome;
        private double totalExpense;
        private double netSavings;
    }

    /** Payment mode breakdown item */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentModeBreakdown {
        private String paymentMode;
        private double totalSpent;
        private double percentage;
    }
}
