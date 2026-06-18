package com.expensewise.controller;

import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.ReportResponse;
import com.expensewise.model.Expense;
import com.expensewise.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** Monthly summary: income, expense, savings, top category, budget status */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportResponse.MonthlySummary>> monthlySummary(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Monthly summary",
                reportService.getMonthlySummary(userId, month, year)));
    }

    /** Yearly summary: best/worst month, average, totals */
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse<ReportResponse.YearlySummary>> yearlySummary(
            HttpServletRequest request,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Yearly summary",
                reportService.getYearlySummary(userId, year)));
    }

    /** Category breakdown sorted by spend descending with percentages */
    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<List<ReportResponse.CategoryBreakdown>>> categoryBreakdown(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Category breakdown",
                reportService.getCategoryBreakdown(userId, month, year)));
    }

    /** Income vs expense comparison for a month */
    @GetMapping("/income-vs-expense")
    public ResponseEntity<ApiResponse<ReportResponse.IncomeVsExpense>> incomeVsExpense(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Income vs expense",
                reportService.getIncomeVsExpense(userId, month, year)));
    }

    /** Top N expenses for a month, sorted by amount */
    @GetMapping("/top-expenses")
    public ResponseEntity<ApiResponse<List<Expense>>> topExpenses(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "5") int limit) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Top expenses",
                reportService.getTopExpenses(userId, month, year, limit)));
    }

    /** Month-by-month savings trend for the full year (all 12 months) */
    @GetMapping("/savings-trend")
    public ResponseEntity<ApiResponse<List<ReportResponse.SavingsTrend>>> savingsTrend(
            HttpServletRequest request,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Savings trend",
                reportService.getSavingsTrend(userId, year)));
    }

    /** Expense breakdown by payment mode (CASH / UPI / CARD / NETBANKING) */
    @GetMapping("/payment-mode-breakdown")
    public ResponseEntity<ApiResponse<List<ReportResponse.PaymentModeBreakdown>>> paymentModeBreakdown(
            HttpServletRequest request,
            @RequestParam int month,
            @RequestParam int year) {
        String userId = (String) request.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success("Payment mode breakdown",
                reportService.getPaymentModeBreakdown(userId, month, year)));
    }
}
