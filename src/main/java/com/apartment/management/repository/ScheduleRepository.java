package com.apartment.management.repository;

import com.apartment.management.model.Schedule;
import com.apartment.management.model.Schedule.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStatusOrderByStartDateAsc(ScheduleStatus status);

    @Query("SELECT s FROM Schedule s WHERE s.startDate BETWEEN :start AND :end ORDER BY s.startDate")
    List<Schedule> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
