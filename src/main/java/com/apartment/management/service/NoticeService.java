package com.apartment.management.service;

import com.apartment.management.model.Notice;
import com.apartment.management.model.Notice.NoticeStatus;
import com.apartment.management.model.User;
import com.apartment.management.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회
     */
    public Page<Notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findByStatusOrderByIsPinnedDescCreatedAtDesc(NoticeStatus.ACTIVE, pageable);
    }

    /**
     * 공지사항 상세 조회
     */
    @Transactional
    public Notice getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다"));

        // 조회수 증가
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);

        return notice;
    }

    /**
     * 공지사항 생성
     */
    @Transactional
    public Notice createNotice(Notice notice, User author) {
        notice.setAuthor(author);
        notice.setStatus(NoticeStatus.ACTIVE);
        Notice saved = noticeRepository.save(notice);
        log.info("공지사항 생성: {} by {}", saved.getId(), author.getUsername());
        return saved;
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다"));

        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        notice.setIsPinned(updatedNotice.getIsPinned());

        Notice saved = noticeRepository.save(notice);
        log.info("공지사항 수정: {}", id);
        return saved;
    }

    /**
     * 공지사항 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다"));

        notice.setStatus(NoticeStatus.DELETED);
        noticeRepository.save(notice);
        log.info("공지사항 삭제: {}", id);
    }

    /**
     * 공지사항 상단 고정 토글
     */
    @Transactional
    public Notice togglePin(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다"));

        notice.setIsPinned(!notice.getIsPinned());
        Notice saved = noticeRepository.save(notice);
        log.info("공지사항 고정 토글: {} -> {}", id, saved.getIsPinned());
        return saved;
    }
}
