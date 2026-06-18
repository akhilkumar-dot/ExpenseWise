package com.expensewise.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Expense document stored in the 'expenses' MongoDB collection.
 * Denormalizes categoryName for fast reads without joins.
 */
@Document(collection = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

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

    /** Denormalized for display — avoids extra DB lookup */
    @Field("category_name")
    private String categoryName;

    /** CASH / UPI / CARD / NETBANKING */
    @Field("payment_mode")
    private String paymentMode;

    @Field("note")
    private String note;

    /** Date string in yyyy-MM-dd format */
    @Field("date")
    private String date;

    /** Extracted month for easy aggregation filtering */
    @Field("month")
    private int month;

    /** Extracted year for easy aggregation filtering */
    @Field("year")
    private int year;

    /** Flexible tags for keyword search */
    @Field("tags")
    private List<String> tags;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
