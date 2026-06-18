package com.expensewise.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse {

    private String id;
    private String userId;
    private String title;
    private double amount;
    private String categoryId;
    private String categoryName;
    private String source;
    private String note;
    private String date;
    private int month;
    private int year;
    private LocalDateTime createdAt;
}
