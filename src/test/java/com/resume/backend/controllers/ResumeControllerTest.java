package com.resume.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.controller.ResumeController;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.services.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResumeController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Disabled
public class ResumeControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ResumeService resumeService;
    MockMultipartFile mockFile;
    @BeforeEach()
    void  setUpData(){
         mockFile = new MockMultipartFile(
                "file",
                "test_resume.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Mock PDF content".getBytes()
        );
    }
    @Test
    @DisplayName("uploadig the resume")
    void uploadResumeTest() throws Exception {
        String originalFilename = "test_resume.pdf";
        String fileContent = "Mock PDF content";
        
        Resume resume = new Resume();
        resume.setId(1L);
        resume.setUserId(1L);
        resume.setName("Habibulla");
        resume.setAddress("Tenali");
        resume.setOriginalFileName("Names");
        when(resumeService.uploadResume(anyLong(), any(MultipartFile.class)))
                .thenReturn(resume);
        RequestBuilder requestBuilder = multipart(HttpMethod.POST,"/ai/upload")
                .file(mockFile)
                .param("userId", "123")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        System.out.println("Hello "+ response.getContentAsString());

        // Assertions
        assertEquals("Resume uploaded successfully. ID: "+resume.getUserId(), response.getContentAsString());
        assertEquals(HttpStatus.OK.value(),response.getStatus());


    }
    @Test
    @DisplayName("Exception Test for the file upload")
    void uploadResumeTest_ThrowException() throws Exception {
        when(resumeService.uploadResume(anyLong(),any(MultipartFile.class))).thenThrow(new RuntimeException("File processing failed"));
        RequestBuilder requestBuilder = multipart(HttpMethod.POST,"/ai/upload")
                .file(mockFile)
                .param("userId", "123")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println("contentAsString "+ contentAsString);
        assertEquals("Upload failed: File processing failed" ,contentAsString);
        assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus());

    }
    @Test
    @DisplayName("Test case for Invalid Resume file")
    void uploadResume_InvalidResume_Exception() throws Exception {
        when(resumeService.uploadResume(anyLong(),any(MultipartFile.class))).thenReturn(null).thenThrow(new RuntimeException("resume is null"));
        RequestBuilder requestBuilder = multipart(HttpMethod.POST,"/ai/upload")
                .file(mockFile)
                .param("userId", "123")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println("contentAsString "+ contentAsString);
        assertEquals("Resume is null",contentAsString);
        assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus());
    }
    //Test for resume screening
    @Test
    @DisplayName("Test case for screening the resume using ai")
    void resumeScreenTest() throws Exception {
        ResumeAnalysisDTO resumeAnalysisDTO1 = new ResumeAnalysisDTO();
        resumeAnalysisDTO1.setMatchPercentage(80);
        resumeAnalysisDTO1.setExtractedSkills(List.of("Java","Angular","Spring boot"));
        resumeAnalysisDTO1.setName("Habibulla");
        resumeAnalysisDTO1.setAddress("Angalakuduru");
        resumeAnalysisDTO1.setYearsOfExperience(2.5);
        resumeAnalysisDTO1.setSuggestions(List.of("Do certification","Add more projects to resume"));
        resumeAnalysisDTO1.setConclusion("Resume is good fit for this Job description");
        List<ResumeAnalysisDTO> resumeAnalysisDTOList = new ArrayList<>();
        resumeAnalysisDTOList.add(resumeAnalysisDTO1);

        when(resumeService.resumeScreen(anyString(),anyBoolean())).thenReturn(resumeAnalysisDTOList);
        RequestBuilder requestBuilder= post("/ai/screen-resume")
                .param("jobRole", "Developer")
                .param("scanAllresumesIsChecked", "true")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        //Assertions
        String content = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        List<ResumeAnalysisDTO> actualList = objectMapper.readValue(content, new TypeReference<List<ResumeAnalysisDTO>>() {});
        assertEquals(resumeAnalysisDTOList.size(),actualList.size());
        assertEquals(resumeAnalysisDTOList.get(0).getName(),actualList.get(0).getName());

    }
    @Test
    @DisplayName("Invalid Json format Exception")
    void resumeScreen_InvalidJsonFormatTest() throws Exception {
        when(resumeService.resumeScreen(anyString(),anyBoolean())).thenThrow(new JsonProcessingException("Invalid JSON format") {});
        RequestBuilder requestBuilder= post("/ai/screen-resume")
                .param("jobRole", "Developer")
                .param("scanAllresumesIsChecked", "true")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        //Assertions
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Content "+ content);
        ProblemDetail problemDetail = new ObjectMapper().readValue(content, ProblemDetail.class);
        assertEquals("Invalid JSON Format",problemDetail.getTitle());
        assertEquals("Not a valid json format",problemDetail.getDetail());
        assertEquals(HttpStatus.BAD_REQUEST.value(),problemDetail.getStatus());

    }
    @Test
    @DisplayName("Getting all the dashboard Deatils")
    void getAllDashboardDetailsTest() throws Exception {
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setCanditateScanned(2);
        dashboardDto.setTotalResumes(3);
        dashboardDto.setTotalResumePercentage(60);
        dashboardDto.setBestMatch(80);
        when(resumeService.getAllDashboardDetails()).thenReturn(dashboardDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/ai/gellAllDashboardDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dashboardDto));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        DashboardDto dashboardDtoResult = new ObjectMapper().readValue(contentAsString, DashboardDto.class);
        assertEquals(dashboardDtoResult.getCanditateScanned(),2);



    }
}
