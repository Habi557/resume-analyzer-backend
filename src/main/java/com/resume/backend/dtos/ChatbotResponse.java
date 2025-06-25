package com.resume.backend.dtos;

import com.resume.backend.entity.Resume;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
public class ChatbotResponse {
   public String answer;
   public List<Resume> resumes;

}
