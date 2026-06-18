package com.expensewise.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Budget document — tracks spending limit for a category in a given month/year.
 * spentAmount and remainingAmount are recalculated whenever an expense is added/updated.
 */
@Document(collection = "budgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("category_id")
    private String categoryId;

    /** Denormalized for display */
    @Field("category_name")
    private String categoryName;

    @Field("budget_amount")
    private double budgetAmount;

    /** Updated dynamically when expenses are added/modified */
    @Field("spent_amount")
    @Builder.Default
    private double spentAmount = 0.0;

    /** remainingAmount = budgetAmount - spentAmount */
    @Field("remaining_amount")
    @Builder.Default
    private double remainingAmount = 0.0;

    @Field("month")
    private int month;

    @Field("year")
    private int year;

    /** Percentage threshold at which a warning is triggered (default 80%) */
    @Field("alert_threshold")
    @Builder.Default
    private int alertThreshold = 80;

    /** True when spentAmount >= budgetAmount */
    @Field("is_exceeded")
    @Builder.Default
    private boolean isExceeded = false;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
