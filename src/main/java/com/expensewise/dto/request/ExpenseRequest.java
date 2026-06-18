package com.expensewise.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Positive(message = "Amount must be positive")
    private double amount;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    /** CASH / UPI / CARD / NETBANKING */
    @NotBlank(message = "Payment mode is required")
    @Pattern(regexp = "CASH|UPI|CARD|NETBANKING", message = "Invalid payment mode")
    private String paymentMode;

    private String note;

    @NotBlank(message = "Date is required (yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd format")
    private String date;

    private List<String> tags;
}
