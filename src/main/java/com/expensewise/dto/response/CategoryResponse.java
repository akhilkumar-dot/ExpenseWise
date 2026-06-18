package com.expensewise.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private String id;
    private String userId;
    private String name;
    private String type;
    private String icon;
    private String colorCode;
    private boolean isDefault;
    private LocalDateTime createdAt;
}
