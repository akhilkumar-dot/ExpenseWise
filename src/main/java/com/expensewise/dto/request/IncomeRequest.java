package com.expensewise.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class IncomeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Positive(message = "Amount must be positive")
    private double amount;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    /** SALARY / FREELANCE / INVESTMENT / GIFT / OTHER */
    @NotBlank(message = "Source is required")
    @Pattern(regexp = "SALARY|FREELANCE|INVESTMENT|GIFT|OTHER", message = "Invalid income source")
    private String source;

    private String note;

    @NotBlank(message = "Date is required (yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd format")
    private String date;
}
