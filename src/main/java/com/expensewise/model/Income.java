package com.expensewise.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Income document stored in the 'incomes' MongoDB collection.
 */
@Document(collection = "incomes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("title")
    private String title;

    @Field("amount")
    private double amount;

    @Field("category_id")
    private String categoryId;

    /** Denormalized category name for fast display */
    @Field("category_name")
    private String categoryName;

    /** SALARY / FREELANCE / INVESTMENT / GIFT / OTHER */
    @Field("source")
    private String source;

    @Field("note")
    private String note;

    /** Date string in yyyy-MM-dd format */
    @Field("date")
    private String date;

    @Field("month")
    private int month;

    @Field("year")
    private int year;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
