package com.apartment.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String name;
    private String phoneNumber;
    private String role;
    private boolean requiresSmsVerification;
    private String message;
    private String verificationCode; // 개발 환경에서만 사용

    public AuthResponse(String token, Long userId, String username, String name, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
        this.requiresSmsVerification = false;
    }
}
