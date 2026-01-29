package com.apartment.management.service;

import com.apartment.management.model.SmsVerification;
import com.apartment.management.repository.SmsVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class SmsService {

    @Autowired
    private SmsVerificationRepository smsVerificationRepository;

    @Value("${sms.api.key:default-key}")
    private String smsApiKey;

    @Value("${sms.api.url:default-url}")
    private String smsApiUrl;

    /**
     * SMS 인증 코드 전송
     */
    @Transactional
    public String sendVerificationCode(String phoneNumber) {
        // 6자리 랜덤 코드 생성
        String code = generateVerificationCode();

        // 데이터베이스에 저장
        SmsVerification verification = SmsVerification.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .build();
        smsVerificationRepository.save(verification);

        // 실제 SMS 전송 (여기서는 로그로 대체)
        sendSms(phoneNumber, code);

        return code; // 개발 환경에서만 코드 반환
    }

    /**
     * SMS 인증 코드 검증
     */
    @Transactional
    public boolean verifyCode(String phoneNumber, String code) {
        SmsVerification verification = smsVerificationRepository
                .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(phoneNumber)
                .orElse(null);

        if (verification == null) {
            log.warn("인증 코드를 찾을 수 없습니다: {}", phoneNumber);
            return false;
        }

        if (verification.isExpired()) {
            log.warn("인증 코드가 만료되었습니다: {}", phoneNumber);
            return false;
        }

        if (!verification.getCode().equals(code)) {
            log.warn("인증 코드가 일치하지 않습니다: {}", phoneNumber);
            return false;
        }

        // 인증 성공
        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        smsVerificationRepository.save(verification);

        log.info("SMS 인증 성공: {}", phoneNumber);
        return true;
    }

    /**
     * 6자리 랜덤 인증 코드 생성
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 실제 SMS 전송 (현재는 로그로 대체)
     * 실제 환경에서는 SMS API를 호출하도록 구현
     */
    private void sendSms(String phoneNumber, String code) {
        // TODO: 실제 SMS API 연동
        log.info("==================================================");
        log.info("SMS 발송");
        log.info("수신번호: {}", phoneNumber);
        log.info("인증코드: {}", code);
        log.info("==================================================");

        // 실제 SMS API 호출 예시:
        // RestTemplate restTemplate = new RestTemplate();
        // HttpHeaders headers = new HttpHeaders();
        // headers.set("Authorization", "Bearer " + smsApiKey);
        // Map<String, String> body = new HashMap<>();
        // body.put("to", phoneNumber);
        // body.put("message", "[아파트관리시스템] 인증번호: " + code);
        // HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        // restTemplate.postForObject(smsApiUrl, request, String.class);
    }

    /**
     * 만료된 인증 코드 삭제 (스케줄러로 주기적 실행 가능)
     */
    @Transactional
    public void deleteExpiredCodes() {
        smsVerificationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("만료된 인증 코드 삭제 완료");
    }
}
