package com.apartment.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String requesterName; // 민원인 이름

    @Column(nullable = false, length = 20)
    private String requesterPhone; // 민원인 전화번호

    @Column(length = 50)
    private String dong; // 동

    @Column(length = 50)
    private String ho; // 호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedTo; // 담당자

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<ComplaintComment> comments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ComplaintCategory {
        NOISE,          // 소음
        PARKING,        // 주차
        FACILITY,       // 시설
        CLEANING,       // 청소
        SECURITY,       // 보안
        ELEVATOR,       // 엘리베이터
        WATER,          // 수도
        HEATING,        // 난방
        OTHER           // 기타
    }

    public enum ComplaintStatus {
        PENDING,        // 접수
        IN_PROGRESS,    // 처리중
        COMPLETED,      // 처리완료
        REJECTED        // 반려
    }
}
