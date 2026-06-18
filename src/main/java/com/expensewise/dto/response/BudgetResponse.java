package com.expensewise.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

    private String id;
    private String userId;
    private String categoryId;
    private String categoryName;
    private double budgetAmount;
    private double spentAmount;
    private double remainingAmount;
    private int month;
    private int year;
    private int alertThreshold;
    private boolean isExceeded;
    private LocalDateTime createdAt;
}
