package com.resume.backend.servicestest;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.serviceImplementation.ResumeServiceImplementation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ResumeServiceTest {
    @Mock
    ResumeRepository resumeRepository;
    @Mock
    ChatClient chatClient;
    @Mock
    ResumeHelper resumeHelper;
    @Mock
   ResumeAnalysis resumeAnalysis;
   private static Long userId;
//    @Mock
//    ModelMapper modelMapper;
    @InjectMocks
    ResumeServiceImplementation resumeService;



    List<Resume> resumeList;
    private List<ResumeAnalysisDTO> listOfresumeAnalysisDTO;
    private String templateFile;


    @BeforeAll
    static void initAll(){
         userId= 123L;

    }
    @BeforeEach
    void  init(){
    resumeList = new ArrayList<>();

        Resume resume1 = new Resume();
        resume1.setId(1L);
        resume1.setUserId(101L);
        resume1.setName("Sk Habibulla");
        resume1.setExtractedSkills(List.of("Java", "Spring Boot", "Hibernate", "Docker"));
        resume1.setYearsOfExperience(3.5);
        resume1.setOriginalFileName("resume_sk_habibulla.pdf");
        resume1.setFilePath("/test/uploads/resume_sk_habibulla.pdf");
        resume1.setAddress("Kolkata, West Bengal");
        resume1.setExtractedText("Sk Habibulla is a skilled Java developer with experience in Spring Boot, Docker, and Microservices.");
        resume1.setUploadTime(LocalDateTime.of(2025, 4, 21, 10, 30));
        resume1.setScanAllresumesIsChecked(true);

        Resume resume2 = new Resume();
        resume2.setId(2L);
        resume2.setUserId(102L);
        resume2.setName("Ayesha Khan");
        resume2.setExtractedSkills(List.of("Python", "Django", "PostgreSQL"));
        resume2.setYearsOfExperience(2.0);
        resume2.setOriginalFileName("resume_ayesha.pdf");
        resume2.setFilePath("/test/uploads/resume_ayesha.pdf");
        resume2.setAddress("Hyderabad, Telangana");
        resume2.setExtractedText("Ayesha is a Python developer experienced in Django and PostgreSQL.");
        resume2.setUploadTime(LocalDateTime.of(2025, 4, 20, 9, 15));
        resume2.setScanAllresumesIsChecked(false);

        resumeList.addAll(List.of(resume1, resume2));
        /// ///////////////////////////////////////////////
       // resumeScreenTest data setup
         listOfresumeAnalysisDTO= new ArrayList<>();
        ResumeAnalysisDTO dto = new ResumeAnalysisDTO();
        dto.setMatchPercentage(85);
        dto.setExtractedSkills(List.of("Java", "Spring Boot", "Docker", "REST API"));
        dto.setName("Sk Habibulla");
        dto.setAddress("Kolkata, West Bengal");
        dto.setYearsOfExperience(3.5);
        dto.setSuggestions(List.of(
                "Consider adding experience with Kubernetes.",
                "Improve documentation skills.",
                "Get certified in Spring Security."
        ));
        dto.setConclusion("The candidate has strong backend skills with good experience in Spring Boot and Docker. Slight improvement in DevOps and security aspects is recommended.");
        listOfresumeAnalysisDTO.add(dto);
        /// ///

         templateFile = "You are an AI Resume Screener. Analyze the following provided resumeText and job role below is fit or not.\n"
                + "ResumeText is {resumeText} and job role is {jobRole}\n"
                + "Return the response in JSON format with the following structure and don't add any text before or after the json\n"
                + "{\n"
                + "    \"name\": \"Name of the resume \",\n"
                + "    \"address\": \"give only the city of the resume holder as string\",\n"
                + "    \"matchPercentage\": \"percentage value of match between 0 to 100 integer value \",\n"
                + "    \"Extracted skills\": [\"list\", \"of\", \"skills\"],\n"
                + "    \"yearsOfExperience\": \"give the integer value\",\n"
                + "    \"suggestions\": [\"list of suggestions as string format\"],\n"
                + "    \"analysis\": \"analysis response as string\"\n"
                + "}\n"
                + "IMPORTANT: Your response must be pure JSON without any additional text and don't provide any comments if the fields are empty or something else.\n"
                + "IMPORTANT: Don't add any comments for the JSON";

    }
    @Test
  //  @Disabled("Making the function disabled temp")
    void  uploadResumeTest() throws IOException {
        Long userId = 1L;
        String originalFilename = "test_resume.pdf";
        String fileContent = "Mock PDF content";
        Resume testResume = new Resume();
        testResume.setName("Habibulla");
        testResume.setId(1L);
        testResume.setOriginalFileName("test_resume.pdf");

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                originalFilename,
                "application/pdf",
                fileContent.getBytes()
        );

        // Act
       // String fileName = mockFile.getOriginalFilename();

       // when(resumeService.uploadResume(userId,mockFile)).thenReturn(testResume);
        when(resumeHelper.extractTextFromPdf(any(File.class))).thenReturn("My text from the resume");
        when(resumeHelper.loadPromptTemplate(anyString())).thenReturn("My text from the Template");
        Resume resumeOutPut = resumeService.uploadResume(userId, mockFile);
        Assertions.assertNotNull(resumeOutPut);
        assertEquals(1L,resumeOutPut.getId());
        assertEquals(originalFilename,resumeOutPut.getOriginalFileName());

    }
    @Test
    //@Disabled
    void  resumeScreenTest() throws IOException {
        String stringJson = """
{
  "matchPercentage": 85,
  "extractedSkills": ["Java", "Spring Boot", "Docker", "REST API"],
  "name": "Sk Habibulla",
  "address": "Kolkata, West Bengal",
  "yearsOfExperience": 3.5,
  "suggestions": [
    "Consider adding experience with Kubernetes.",
    "Improve documentation skills.",
    "Get certified in Spring Security."
  ],
  "conclusion": "The candidate has strong backend skills with good experience in Spring Boot and Docker. Slight improvement in DevOps and security aspects is recommended."
}
""";

        when(resumeRepository.findAll()).thenReturn(resumeList);
        when(resumeHelper.loadPromptTemplate(any(String.class))).thenReturn(templateFile);
        when(resumeHelper.putValuesToPrompt("This is my template", Map.of("resumeText","test data","jobRole", "My jobdata"))).thenReturn("My name is shaik Habibulla and Java full stack developer");
        when(resumeService.resumeScreenAI(resumeList,"Java full stack developer")).thenReturn(listOfresumeAnalysisDTO);

         List<ResumeAnalysisDTO> resumeAnalysisDTOS = resumeService.resumeScreen("Java full stack developer", true);





    }
    @ParameterizedTest
   // @ValueSource(strings = {"Habibulla","Abdulla","Badulla","Jainabe"})
    @MethodSource
    void pringting(String name){
        System.out.println(name);
    }
    public static Stream<String> pringting(){
        return Stream.of("Habi","Abdulla");
    }

}
