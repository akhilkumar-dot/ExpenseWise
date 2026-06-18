package com.expensewise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    /** EXPENSE or INCOME */
    @NotBlank(message = "Type is required (EXPENSE or INCOME)")
    private String type;

    private String icon;
    private String colorCode;
}
