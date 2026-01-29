package com.apartment.management.dto;

import com.apartment.management.model.Complaint.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotBlank(message = "민원인 이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하로 입력해주세요")
    private String requesterName;

    @NotBlank(message = "민원인 전화번호는 필수입니다")
    @Pattern(regexp = "^01(?:0|1|[6-9])-?(?:\\d{3}|\\d{4})-?\\d{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String requesterPhone;

    @Size(max = 50, message = "동은 50자 이하로 입력해주세요")
    private String dong;

    @Size(max = 50, message = "호는 50자 이하로 입력해주세요")
    private String ho;

    @NotNull(message = "민원 카테고리는 필수입니다")
    private ComplaintCategory category;
}
