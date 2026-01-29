package com.apartment.management.controller;

import com.apartment.management.dto.ScheduleRequest;
import com.apartment.management.model.Schedule;
import com.apartment.management.model.Schedule.ScheduleStatus;
import com.apartment.management.model.User;
import com.apartment.management.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 일정 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Schedule> schedules;
        if (start != null && end != null) {
            schedules = scheduleService.getSchedulesByDateRange(start, end);
        } else {
            schedules = scheduleService.getAllSchedules();
        }
        return ResponseEntity.ok(schedules);
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @Valid @RequestBody ScheduleRequest request,
            @AuthenticationPrincipal User user) {

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .type(request.getType())
                .status(request.getStatus() != null ? request.getStatus() : ScheduleStatus.SCHEDULED)
                .build();

        Schedule created = scheduleService.createSchedule(schedule, user);
        return ResponseEntity.ok(created);
    }

    /**
     * 일정 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest request) {

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .type(request.getType())
                .status(request.getStatus())
                .build();

        Schedule updated = scheduleService.updateSchedule(id, schedule);
        return ResponseEntity.ok(updated);
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 일정 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Schedule> updateStatus(
            @PathVariable Long id,
            @RequestParam ScheduleStatus status) {
        Schedule schedule = scheduleService.updateStatus(id, status);
        return ResponseEntity.ok(schedule);
    }
}
