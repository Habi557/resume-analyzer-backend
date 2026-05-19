package com.resume.backend.dtos;

import com.resume.backend.entity.JobStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResumeAnalysisPollingDto {
    private int totalResume;
    private int processedResume;
    private int failedResume;
    private JobStatus status;

}
