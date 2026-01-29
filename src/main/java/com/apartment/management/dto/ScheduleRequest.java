package com.apartment.management.dto;

import com.apartment.management.model.Schedule.ScheduleStatus;
import com.apartment.management.model.Schedule.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요")
    private String title;

    private String description;

    @NotNull(message = "시작일시는 필수입니다")
    private LocalDateTime startDate;

    @NotNull(message = "종료일시는 필수입니다")
    private LocalDateTime endDate;

    private String location;

    @NotNull(message = "일정 유형은 필수입니다")
    private ScheduleType type;

    private ScheduleStatus status;
}
