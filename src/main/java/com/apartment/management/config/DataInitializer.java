package com.apartment.management.config;

import com.apartment.management.model.User;
import com.apartment.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // jun 사용자가 없으면 생성
        if (!userRepository.findByUsername("jun").isPresent()) {
            User user = User.builder()
                    .username("jun")
                    .password(passwordEncoder.encode("password123"))
                    .name("박준")
                    .email("jun@apartment.com")
                    .phoneNumber("01012345678")
                    .role(User.UserRole.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(user);
            log.info("초기 사용자 'jun' 생성 완료");
        } else {
            // jun 사용자가 이미 있으면 비밀번호만 업데이트
            User user = userRepository.findByUsername("jun").get();
            user.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(user);
            log.info("사용자 'jun' 비밀번호 업데이트 완료");
        }
    }
}
