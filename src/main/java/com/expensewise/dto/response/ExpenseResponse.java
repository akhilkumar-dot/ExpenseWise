package com.expensewise.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private String id;
    private String userId;
    private String title;
    private double amount;
    private String categoryId;
    private String categoryName;
    private String paymentMode;
    private String note;
    private String date;
    private int month;
    private int year;
    private List<String> tags;
    private LocalDateTime createdAt;

    /** Optional budget warning message populated when threshold is exceeded */
    private String budgetWarning;
}
