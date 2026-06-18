package com.expensewise.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BudgetRequest {

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @Positive(message = "Budget amount must be positive")
    private double budgetAmount;

    @Min(1) @Max(12)
    private int month;

    @Min(2000) @Max(2100)
    private int year;

    /** Alert threshold percentage, default 80 */
    @Min(1) @Max(100)
    private int alertThreshold = 80;
}
