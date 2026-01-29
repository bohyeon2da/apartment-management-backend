package com.apartment.management.repository;

import com.apartment.management.model.Notice;
import com.apartment.management.model.Notice.NoticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByStatusOrderByIsPinnedDescCreatedAtDesc(NoticeStatus status, Pageable pageable);

    Page<Notice> findByStatus(NoticeStatus status, Pageable pageable);
}
