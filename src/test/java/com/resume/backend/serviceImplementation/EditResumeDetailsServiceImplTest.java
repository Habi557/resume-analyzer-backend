package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.EditResumeDeatilsDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditResumeDetailsServiceImplTest {
    @Mock
    ResumeRepository resumeRepository;

    @InjectMocks
    EditResumeDetailsServiceImpl editResumeDetailsServiceImpl;
    Resume resume;
    EditResumeDeatilsDto editResumeDeatilsDto;
    @BeforeEach
    public void datasetup(){
        resume = new Resume();
        resume.setName("Habibulla");
        resume.setYearsOfExperience(2.5);
        resume.setEmail("habi@gmail.com");
        editResumeDeatilsDto = new EditResumeDeatilsDto();
        editResumeDeatilsDto.setId(1L);
        editResumeDeatilsDto.setName("Abdulla");
        editResumeDeatilsDto.setYearsOfExperience(3.5);
        editResumeDeatilsDto.setEmail("habi123@gmail.com");

    }
    @Test
    public void testEditResumeDetails() {
        when(resumeRepository.findById(anyLong())).thenReturn(Optional.of(resume));
        String  message = editResumeDetailsServiceImpl.editResumeDetails(editResumeDeatilsDto);
//        resumeRepository.findById(anyLong()).ifPresent(resume -> {
//            resume.setName("Abdulla");
//            resume.setYearsOfExperience(3.5);
//            resume.setEmail("habi123@gmail.com");
//            resumeRepository.save(resume);
//
//        });
        System.out.println(message);
        verify(resumeRepository).save(resume);
        assertEquals("updated sucessfully", message);

    }

}
