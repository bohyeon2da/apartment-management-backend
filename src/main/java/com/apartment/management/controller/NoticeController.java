package com.apartment.management.controller;

import com.apartment.management.dto.NoticeRequest;
import com.apartment.management.model.Notice;
import com.apartment.management.model.User;
import com.apartment.management.service.NoticeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 공지사항 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<Notice>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지사항 생성
     */
    @PostMapping
    public ResponseEntity<Notice> createNotice(
            @Valid @RequestBody NoticeRequest request,
            @AuthenticationPrincipal User user) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.getIsPinned())
                .build();

        Notice created = noticeService.createNotice(notice, user);
        return ResponseEntity.ok(created);
    }

    /**
     * 공지사항 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notice> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.getIsPinned())
                .build();

        Notice updated = noticeService.updateNotice(id, notice);
        return ResponseEntity.ok(updated);
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 공지사항 상단 고정 토글
     */
    @PostMapping("/{id}/toggle-pin")
    public ResponseEntity<Notice> togglePin(@PathVariable Long id) {
        Notice notice = noticeService.togglePin(id);
        return ResponseEntity.ok(notice);
    }
}
