package com.expensewise.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Category document — can be EXPENSE or INCOME type.
 * Each user gets a set of default categories seeded on registration.
 */
@Document(collection = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("name")
    private String name;

    /** EXPENSE or INCOME */
    @Field("type")
    private String type;

    /** Emoji or icon identifier, e.g. "🍕" */
    @Field("icon")
    private String icon;

    /** Hex color code for UI display, e.g. "#FF5733" */
    @Field("color_code")
    private String colorCode;

    /** True for system-seeded categories, false for user-created ones */
    @Field("is_default")
    @Builder.Default
    private boolean isDefault = false;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
