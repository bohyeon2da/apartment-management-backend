package com.apartment.management.controller;

import com.apartment.management.dto.CommentRequest;
import com.apartment.management.dto.ComplaintRequest;
import com.apartment.management.model.Complaint;
import com.apartment.management.model.Complaint.ComplaintStatus;
import com.apartment.management.model.ComplaintComment;
import com.apartment.management.model.User;
import com.apartment.management.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    /**
     * 민원 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<Complaint>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Complaint> complaints;

        if (status != null) {
            complaints = complaintService.getComplaintsByStatus(status, pageable);
        } else {
            complaints = complaintService.getAllComplaints(pageable);
        }

        return ResponseEntity.ok(complaints);
    }

    /**
     * 민원 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        Complaint complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    /**
     * 민원 생성
     */
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .requesterName(request.getRequesterName())
                .requesterPhone(request.getRequesterPhone())
                .dong(request.getDong())
                .ho(request.getHo())
                .category(request.getCategory())
                .build();

        Complaint created = complaintService.createComplaint(complaint);
        return ResponseEntity.ok(created);
    }

    /**
     * 민원 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Complaint> updateStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status,
            @AuthenticationPrincipal User user) {
        Complaint complaint = complaintService.updateStatus(id, status, user);
        return ResponseEntity.ok(complaint);
    }

    /**
     * 민원 담당자 지정
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<Complaint> assignComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Complaint complaint = complaintService.assignComplaint(id, user);
        return ResponseEntity.ok(complaint);
    }

    /**
     * 민원 댓글 목록 조회
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<ComplaintComment>> getComments(@PathVariable Long id) {
        List<ComplaintComment> comments = complaintService.getCommentsByComplaintId(id);
        return ResponseEntity.ok(comments);
    }

    /**
     * 민원 댓글 작성
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<ComplaintComment> createComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {

        ComplaintComment comment = ComplaintComment.builder()
                .content(request.getContent())
                .isInternal(request.getIsInternal())
                .build();

        ComplaintComment created = complaintService.createComment(id, comment, user);
        return ResponseEntity.ok(created);
    }

    /**
     * 민원 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        complaintService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
