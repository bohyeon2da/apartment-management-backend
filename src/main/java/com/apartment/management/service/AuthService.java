package com.apartment.management.service;

import com.apartment.management.dto.AuthResponse;
import com.apartment.management.dto.LoginRequest;
import com.apartment.management.model.User;
import com.apartment.management.repository.UserRepository;
import com.apartment.management.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SmsService smsService;

    /**
     * 1단계: 사용자명/비밀번호 인증
     * 성공 시 SMS 인증 코드 발송
     */
    public AuthResponse authenticate(LoginRequest request) {
        try {
            // 사용자명/비밀번호 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            // SMS 인증 코드 발송
            String code = smsService.sendVerificationCode(user.getPhoneNumber());

            log.info("사용자 {} 인증 성공, SMS 코드 발송: {}", user.getUsername(), user.getPhoneNumber());

            return AuthResponse.builder()
                    .requiresSmsVerification(true)
                    .message("SMS 인증 코드가 발송되었습니다")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .phoneNumber(user.getPhoneNumber())
                    .verificationCode(code) // 개발 환경에서만 사용 (프로덕션에서는 제거 필요)
                    .build();

        } catch (AuthenticationException e) {
            log.error("인증 실패: {}", e.getMessage());
            throw new RuntimeException("사용자명 또는 비밀번호가 올바르지 않습니다");
        }
    }

    /**
     * 2단계: SMS 인증 코드 검증
     * 성공 시 JWT 토큰 발급
     */
    public AuthResponse verifySmsAndGenerateToken(String username, String phoneNumber, String code) {
        // SMS 코드 검증
        if (!smsService.verifyCode(phoneNumber, code)) {
            throw new RuntimeException("인증 코드가 올바르지 않거나 만료되었습니다");
        }

        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user);

        log.info("사용자 {} SMS 인증 성공, 토큰 발급", username);

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name()
        );
    }
}
