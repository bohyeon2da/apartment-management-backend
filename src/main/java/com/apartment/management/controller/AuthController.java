package com.apartment.management.controller;

import com.apartment.management.dto.AuthResponse;
import com.apartment.management.dto.LoginRequest;
import com.apartment.management.dto.SmsVerificationRequest;
import com.apartment.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * 임시: BCrypt 해시 생성 (테스트용)
     */
    @GetMapping("/hash/{password}")
    public ResponseEntity<String> generateHash(@PathVariable String password) {
        String hash = passwordEncoder.encode(password);
        log.info("Generated hash for '{}': {}", password, hash);
        return ResponseEntity.ok(hash);
    }

    /**
     * 로그인 - 1단계: 사용자명/비밀번호 인증 및 SMS 발송
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 시도: {}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 - 2단계: SMS 인증 코드 검증 및 토큰 발급
     */
    @PostMapping("/verify-sms")
    public ResponseEntity<AuthResponse> verifySms(@Valid @RequestBody SmsVerificationRequest request) {
        log.info("SMS 인증 시도: {}", request.getPhoneNumber());

        AuthResponse response = authService.verifySmsAndGenerateToken(
                request.getUsername(),
                request.getPhoneNumber(),
                request.getCode()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 검증용 테스트 엔드포인트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("인증된 사용자입니다");
    }
}
