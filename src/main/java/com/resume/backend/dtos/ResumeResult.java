package com.resume.backend.dtos;
public record ResumeResult(ResumeAnalysisDTO dto, boolean success, String errorMsg) {
    public static ResumeResult ok(ResumeAnalysisDTO dto)      { return new ResumeResult(dto, true, null); }
    public static ResumeResult fail(String msg)                { return new ResumeResult(null, false, msg); }
}
