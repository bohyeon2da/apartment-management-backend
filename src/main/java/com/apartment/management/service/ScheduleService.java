package com.apartment.management.service;

import com.apartment.management.model.Schedule;
import com.apartment.management.model.Schedule.ScheduleStatus;
import com.apartment.management.model.User;
import com.apartment.management.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 모든 일정 조회
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /**
     * 날짜 범위로 일정 조회
     */
    public List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByDateRange(start, end);
    }

    /**
     * 일정 상세 조회
     */
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));
    }

    /**
     * 일정 생성
     */
    @Transactional
    public Schedule createSchedule(Schedule schedule, User creator) {
        schedule.setCreator(creator);
        Schedule saved = scheduleRepository.save(schedule);
        log.info("일정 생성: {} by {}", saved.getId(), creator.getUsername());
        return saved;
    }

    /**
     * 일정 수정
     */
    @Transactional
    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        schedule.setTitle(updatedSchedule.getTitle());
        schedule.setDescription(updatedSchedule.getDescription());
        schedule.setStartDate(updatedSchedule.getStartDate());
        schedule.setEndDate(updatedSchedule.getEndDate());
        schedule.setLocation(updatedSchedule.getLocation());
        schedule.setType(updatedSchedule.getType());
        schedule.setStatus(updatedSchedule.getStatus());

        Schedule saved = scheduleRepository.save(schedule);
        log.info("일정 수정: {}", id);
        return saved;
    }

    /**
     * 일정 삭제
     */
    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        scheduleRepository.delete(schedule);
        log.info("일정 삭제: {}", id);
    }

    /**
     * 일정 상태 변경
     */
    @Transactional
    public Schedule updateStatus(Long id, ScheduleStatus status) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        schedule.setStatus(status);
        Schedule saved = scheduleRepository.save(schedule);
        log.info("일정 상태 변경: {} -> {}", id, status);
        return saved;
    }
}
