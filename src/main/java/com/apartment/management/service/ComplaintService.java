package com.apartment.management.service;

import com.apartment.management.model.Complaint;
import com.apartment.management.model.Complaint.ComplaintStatus;
import com.apartment.management.model.ComplaintComment;
import com.apartment.management.model.User;
import com.apartment.management.repository.ComplaintCommentRepository;
import com.apartment.management.repository.ComplaintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintCommentRepository complaintCommentRepository;

    /**
     * 민원 목록 조회
     */
    public Page<Complaint> getAllComplaints(Pageable pageable) {
        return complaintRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 상태별 민원 조회
     */
    public Page<Complaint> getComplaintsByStatus(ComplaintStatus status, Pageable pageable) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * 민원 상세 조회
     */
    @Transactional
    public Complaint getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다"));

        // 조회수 증가
        complaint.setViewCount(complaint.getViewCount() + 1);
        complaintRepository.save(complaint);

        return complaint;
    }

    /**
     * 민원 생성
     */
    @Transactional
    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.PENDING);
        Complaint saved = complaintRepository.save(complaint);
        log.info("민원 생성: {}", saved.getId());
        return saved;
    }

    /**
     * 민원 상태 변경
     */
    @Transactional
    public Complaint updateStatus(Long id, ComplaintStatus status, User user) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다"));

        complaint.setStatus(status);

        if (status == ComplaintStatus.COMPLETED) {
            complaint.setCompletedAt(LocalDateTime.now());
        }

        // 상태 변경자를 담당자로 지정
        if (complaint.getAssignedTo() == null) {
            complaint.setAssignedTo(user);
        }

        Complaint saved = complaintRepository.save(complaint);
        log.info("민원 상태 변경: {} -> {} by {}", id, status, user.getUsername());
        return saved;
    }

    /**
     * 민원 담당자 지정
     */
    @Transactional
    public Complaint assignComplaint(Long id, User assignedTo) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다"));

        complaint.setAssignedTo(assignedTo);
        Complaint saved = complaintRepository.save(complaint);
        log.info("민원 담당자 지정: {} -> {}", id, assignedTo.getUsername());
        return saved;
    }

    /**
     * 민원 댓글 목록 조회
     */
    public List<ComplaintComment> getCommentsByComplaintId(Long complaintId) {
        return complaintCommentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }

    /**
     * 민원 댓글 작성
     */
    @Transactional
    public ComplaintComment createComment(Long complaintId, ComplaintComment comment, User author) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다"));

        comment.setComplaint(complaint);
        comment.setAuthor(author);

        ComplaintComment saved = complaintCommentRepository.save(comment);
        log.info("민원 댓글 작성: 민원 {} by {}", complaintId, author.getUsername());
        return saved;
    }

    /**
     * 민원 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {
        ComplaintComment comment = complaintCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));

        complaintCommentRepository.delete(comment);
        log.info("민원 댓글 삭제: {}", commentId);
    }
}
