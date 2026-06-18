package com.expensewise.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String userId;
    private String username;
    private String email;
    private String name;
    private String currency;
}
