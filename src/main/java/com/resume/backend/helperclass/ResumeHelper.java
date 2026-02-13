package com.resume.backend.helperclass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.*;
import com.resume.backend.entity.Resume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.net.ConnectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Component
public class ResumeHelper {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+91[-\\s]?)?[6-9]\\d{9}|\\b\\d{10}\\b");
    private static final String JOB_TITLES =
            "developer|engineer|executive|manager|consultant|" +
                    "analyst|architect|lead|tester|intern|associate";



    public  String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    public String extractTextFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String text = stripper.getText(document);
            document.close();
            return normalize(text);
        }
    }
    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[•●▪]", "-")
                .replaceAll("\\r", "")
                .replaceAll("\\n{2,}", "\n")
                .trim();
    }

    //    public String extractTextFromDocx(MultipartFile file) throws IOException {
//        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
//            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
//            return extractor.getText();
//        }
//    }
public String extractTextFromDocx(File savedFile) throws IOException {
    try (FileInputStream fis = new FileInputStream(savedFile);
         XWPFDocument doc = new XWPFDocument(fis);
         XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

        return extractor.getText();
    }
}
    public String extractTextFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {

            String headerText = extractHeaders(doc);
            String tableText  = extractTables(doc);
            String bodyText   = extractBody(doc);

            return headerText + "\n" + tableText + "\n" + bodyText;
        }
    }
    private String extractHeaders(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFHeader header : doc.getHeaderList()) {
            for (XWPFParagraph p : header.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
        }
        return sb.toString();
    }
    private String extractTables(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    sb.append(cell.getText()).append("\n");
                }
            }
        }
        return sb.toString();
    }
    private String extractBody(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph p : doc.getParagraphs()) {
            sb.append(p.getText()).append("\n");
        }
        return sb.toString();
    }


    public  String extractJson(String content) {
        // Remove <think> blocks
        String noThink = content.replaceAll("(?s)<think>.*?</think>", "");

        // Remove triple backticks and any leading markdown
        noThink = noThink.replaceAll("(?s)```json", "")
                .replaceAll("```", "")
                .replaceAll("^\\s*\\*\\*.*?\\*\\*", "") // remove markdown bold
                .trim();

        return noThink;
    }
    // load prompt from classPath
    public  String loadPromptTemplate(String filename) throws IOException {
        Path path = new ClassPathResource(filename).getFile().toPath();
        return Files.readString(path);

    }
    public String loadPromptTemplate2(String filename) {
        try (InputStream is = new ClassPathResource(filename).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + filename, e);
        }
    }
    // put values to prompt
    public  String putValuesToPrompt(String template, Map<String,String> values){
        for(Map.Entry<String,String> entry : values.entrySet()){
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
    public boolean isValidGmail(String email) {
        String regex = "^[\\w.-]+@gmail\\.com$";
        return email.matches(regex);
    }

    public Map<ResumeSection, String> detectSections(String rawText) {

        String text = normalizeForDetection(rawText);

        Map<ResumeSection, Integer> positions = new HashMap<>();

        for (ResumeSection section : ResumeSection.values()) {
            for (String keyword : section.keywords) {

                // Match whole line header, ignoring spaces
                Pattern pattern = Pattern.compile(
                        "(?m)^\\s*" + Pattern.quote(keyword) + "\\s*$"
                );

                Matcher matcher = pattern.matcher(text);

                if (matcher.find()) {
                    positions.put(section, matcher.start());
                    break;
                }
            }
        }

        // Sort sections by appearance order
        List<Map.Entry<ResumeSection, Integer>> sorted =
                positions.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .toList();

        Map<ResumeSection, String> result = new LinkedHashMap<>();

        for (int i = 0; i < sorted.size(); i++) {

            ResumeSection current = sorted.get(i).getKey();
            int start = sorted.get(i).getValue();

            int end = (i + 1 < sorted.size())
                    ? sorted.get(i + 1).getValue()
                    : text.length();

            result.put(current, text.substring(start, end).trim());
        }

        return result;
    }
    private String normalizeForDetection(String text) {
        return text.toLowerCase()
                .replaceAll("\\r", "")
                .replaceAll("[\\t ]+", " ")   // collapse spaces
                .replaceAll("\\n\\s*", "\n")  // trim newline spaces
                .replaceAll("\\n{2,}", "\n")  // collapse blank lines
                .trim();
    }
    public ResumeJson buildResumeJson(String fullText, Map<ResumeSection, String> sectionMap) {

        ResumeJson resumeJson = new ResumeJson();

        //  HEADER (not a ResumeSection)
        String headerText = extractHeaderFromResume(fullText);
        HeaderJson header = new HeaderJson();

        header.setName(extractNameFromHeader(headerText));
        header.setEmail(extractEamilFromHeader(headerText));
        header.setPhone(extractPhoneFromHeader(headerText));
        header.setCity(extractCityOnly(headerText));
        header.setYearsOfExperience(extractYearsOfExperience(headerText));

        resumeJson.setHeader(header);

        //  EXPERIENCE
        String expText = sectionMap.getOrDefault(
                ResumeSection.EXPERIENCE, ""
        );

        resumeJson.setExperience(extractExperienceJson(expText));

        //  EDUCATION
        String eduText = sectionMap.getOrDefault(
                ResumeSection.EDUCATION, ""
        );
        List<EducationJson> educationJsons = extractEducationJson(eduText);
        resumeJson.setEducation(educationJsons);

        //  PROJECTS
        String projectText = sectionMap.getOrDefault(
                ResumeSection.PROJECTS, ""
        );

        resumeJson.setProjects(extractProjectJson(projectText));

        //  SKILLS
        String skillsText = sectionMap.getOrDefault(
                ResumeSection.SKILLS, ""
        );

        resumeJson.setSkills(extractSkillsJson(skillsText));

        //  CERTIFICATIONS
        String certText = sectionMap.getOrDefault(
                ResumeSection.CERTIFICATIONS, ""
        );


        resumeJson.setCertifications(extractCertificationsJson(certText));

        return resumeJson;
    }
    public String extractHeaderFromResume(String extractedText) {
        String[] listofLines = extractedText.split("\n");
        String header = String.join("\n", Arrays.stream(listofLines).limit(8).toList());
        return header;
    }
    public String extractNameFromHeader(String headerText) {

        for (String line : headerText.split("\n")) {

            String cleaned = line.trim();
            if (cleaned.isEmpty()) continue;

            String lower = cleaned.toLowerCase();

            // Skip contact & sections
            if (lower.matches(".*(@|summary|experience|skills).*"))
                continue;

            // Skip phone-like lines
            if (cleaned.matches(".*\\d{3,}.*"))
                continue;

            //  Skip job titles
            if (lower.matches(".*(" + JOB_TITLES + ").*"))
                continue;

            String titleCase = toTitleCase(cleaned);

            // Name pattern
            if (titleCase.matches(
                    "^[A-Z][a-z]+([\\s.-][A-Z][a-z]+){0,3}$"
            )) {
                return titleCase;
            }
        }
        return "";
    }
    private String toTitleCase(String input) {
        StringBuilder sb = new StringBuilder();
        for (String word : input.split("\\s+")) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
    public String extractEamilFromHeader(String headerText) {
        Matcher matcher = EMAIL_PATTERN.matcher(headerText);

        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    public String extractPhoneFromHeader(String headerText) {
        Matcher matcher = PHONE_PATTERN.matcher(headerText);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    public static String extractCityOnly(String headerText) {

        List<String> knownCities = List.of(
                "hyderabad", "bangalore", "chennai", "pune",
                "mumbai", "delhi", "kolkata", "ahmedabad"
        );

        for (String line : headerText.split("\n")) {

            String cleaned = line.trim().toLowerCase();
            if (cleaned.isEmpty()) continue;

            // Skip contact/address-heavy lines
            if (cleaned.matches(".*(@|\\d{3,}|road|street|sector|block|pin).*"))
                continue;

            for (String city : knownCities) {
                if (cleaned.contains(city)) {
                    return capitalize(city);
                }
            }
        }
        return "";
    }
    public  double extractYearsOfExperience(String headerText) {
        final Pattern YEARS_PATTERN = Pattern.compile(
                "(\\d+(?:\\.\\d+)?)\\s*(\\+)?\\s*(years?|yrs?)\\s*(of)?\\s*(total)?\\s*(experience|exp)",
                Pattern.CASE_INSENSITIVE);

        Matcher m = YEARS_PATTERN.matcher(headerText);
        if (m.find()) return Double.parseDouble(m.group(1));
       // return extractFromDateRanges(headerText);
        return 0;
    }

    public static List<ExperienceJson> extractExperienceJson(String text) {

        List<ExperienceJson> experiences = new ArrayList<>();
        ExperienceJson current = null;

        for (String line : text.split("\n")) {

            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.matches("(?i)work experience|experience|employment")) continue;

            // Company + dates
            if (line.matches("(?i).*(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*\\d{4}.*")) {

                current = new ExperienceJson();
                current.setCompany(
                        line.replaceAll("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*", "").trim()
                );

                List<String> dates = extractDates(line);
                current.setStartDate(dates.size() > 0 ? dates.get(0) : "");
                current.setEndDate(dates.size() > 1 ? dates.get(1) : "");

                current.setResponsibilities(new ArrayList<>());
                experiences.add(current);
                continue;
            }

            // Role
            if (current != null &&
                    (current.getRole() == null || current.getRole().isEmpty()) &&
                    line.matches("(?i).*(engineer|developer|executive|analyst).*")) {

                current.setRole(capitalize(line));
                continue;
            }

            // Responsibilities
            if (current != null && line.startsWith("-")) {
                current.getResponsibilities().add(cleanBullet(line));
            }
        }
        return experiences;
    }
    public static List<EducationJson> extractEducationJson(String text) {

        List<EducationJson> educations = new ArrayList<>();
        EducationJson current = null;

        for (String line : text.split("\n")) {

            line = line.trim();
            if (line.isEmpty()) continue;

            // Degree detection (grouped regex)
            if (line.matches("(?i).*(bachelor|master|b\\.tech|m\\.tech|degree|diploma).*")) {
                current = new EducationJson();
                current.setDegree(capitalize(line));
                educations.add(current);
            }

            // Duration (year range)
            else if (current != null && line.matches(".*(19|20)\\d{2}.*(19|20)\\d{2}.*")) {
                current.setDuration(line);
            }

            // Score
            else if (current != null && line.matches(".*(cgpa|gpa|%).*")) {
                current.setScore(line);
            }

            // Institute (heuristic)
            else if (current != null && current.getInstitute() == null &&
                    line.matches("(?i).*(college|university|institute|school).*")) {
                current.setInstitute(capitalize(line));
            }
        }

        return educations;
    }
    public static List<ProjectJson> extractProjectJson(String text) {

        List<ProjectJson> projects = new ArrayList<>();
        ProjectJson current = null;

        for (String line : text.split("\n")) {

            line = line.trim();
            if (line.isEmpty()) continue;

            // Project title
            if (line.length() < 40 && !line.startsWith("-")) {
                current = new ProjectJson();
                current.setTitle(capitalize(line));
                current.setDescription(new ArrayList<>());
                projects.add(current);
                continue;
            }

            // Description
            if (current != null) {
                current.getDescription().add(cleanBullet(line));
            }
        }
        return projects;
    }
    public static List<String> extractSkillsJson(String text) {

        Set<String> skills = new LinkedHashSet<>();

        for (String line : text.split("\n")) {

            if (!line.contains(":")) continue;

            String[] parts = line.split(":", 2);

            if (parts.length < 2 || parts[1].isBlank()) {
                continue; // prevent IndexOutOfBounds
            }

            Arrays.stream(parts[1].split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(ResumeHelper::capitalize)
                    .forEach(skills::add);
        }

        return new ArrayList<>(skills);
    }
    public static List<String> extractCertificationsJson(String text) {

        return Arrays.stream(text.split("\n"))
                .map(String::trim)
                .filter(l -> l.length() > 5)
                .filter(l -> !l.equalsIgnoreCase("certifications"))
                .map(ResumeHelper::capitalize)
                .toList();
    }


    private static List<String> extractDates(String line) {
        List<String> dates = new ArrayList<>();
        Matcher m = Pattern.compile(
                "(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\s+\\d{4}"
        ).matcher(line);
        while (m.find()) dates.add(capitalize(m.group()));
        return dates;
    }
    private static String cleanBullet(String line) {
        return line.replaceFirst("^-", "").trim();
    }
    private static String capitalize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(Collectors.joining(" "));
    }


    public static EnumSet<Field> detectMissingFields(ResumeJson resumeJson) {
        EnumSet<Field>  missingFields = EnumSet.noneOf(Field.class);


            if(resumeJson.getHeader().getName().isEmpty()) missingFields.add(Field.NAME);
            if(resumeJson.getHeader().getEmail().isEmpty()) missingFields.add(Field.EMAIL);
            if(resumeJson.getHeader().getPhone().isEmpty()) missingFields.add(Field.PHONE);
            if(resumeJson.getHeader().getCity().isEmpty()) missingFields.add(Field.ADDRESS);
            if(resumeJson.getHeader().getYearsOfExperience()==0) missingFields.add(Field.YEARSOFEXPERIENCE);
           // if(resumeJson.getSkills().isEmpty()) missingFields.add(Field.SKILLS);
          //  if(resumeJson.) missingFields.add(Field.ADDRESS);
        // 🔹 EXPERIENCE
        if (resumeJson.getExperience() == null || resumeJson.getExperience().isEmpty()) {
            missingFields.add(Field.EXPERIENCE);
        }
//        else {
//            boolean roleMissing = resumeJson.getExperience()
//                    .stream()
//                    .anyMatch(e -> isBlank(e.getRole()));
//            if (roleMissing) missingFields.add("experience.role");
//        }

        // 🔹 EDUCATION
        if (resumeJson.getEducation() == null || resumeJson.getEducation().isEmpty()) {
            missingFields.add(Field.EDUCATION);
        }

        // 🔹 SKILLS
        if (resumeJson.getSkills() == null || resumeJson.getSkills().size() < 3) {
            missingFields.add(Field.SKILLS);
        }
        // 🔹 PROJECTS
        if (resumeJson.getProjects() == null || resumeJson.getProjects().isEmpty()) {
            missingFields.add(Field.PROJECTS);
        }

        // 🔹 CERTIFICATIONS
        if (resumeJson.getCertifications() == null || resumeJson.getCertifications().isEmpty()) {
            missingFields.add(Field.CERTIFICATIONS);
        }
        missingFields.add(Field.REDFLAGS);

        return missingFields;


    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static String buildPromptFromEnum(Set<Field> missingFields, String resumeText) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
    Extract ONLY the following missing fields.
    Return ONLY valid JSON.
    Do NOT modify existing values.

    Missing fields:
    """);

        missingFields.forEach(f ->
                sb.append("- ").append(f.name().toLowerCase()).append("\n")
        );

        sb.append("\nJSON format:\n{\n");
        missingFields.forEach(f -> {
            sb.append("  \"")
                    .append(f.name().toLowerCase())
                    .append("\": ")
                    .append(jsonTemplateForField(f))
                    .append(",\n");
        });
        sb.append("}\n\nResume:\n");
        sb.append(resumeText);

        return sb.toString();
    }
    public static ResumeJson mergeAiUsingEnum(ResumeJson resumeJson, Map<String, Object> aiMap, Set<Field> missingFields) {

        for (Field field : missingFields) {

            String key = jsonKey(field);

            if (!aiMap.containsKey(key)) continue;

            Object value = aiMap.get(key);

            switch (field) {

                case NAME -> {
                    if (isBlank(resumeJson.getHeader().getName())) {
                        resumeJson.getHeader().setName(value.toString());
                    }
                }

                case EMAIL -> {
                    if (isBlank(resumeJson.getHeader().getEmail())) {
                        resumeJson.getHeader().setEmail(value.toString());
                    }
                }

                case PHONE -> {
                    if (isBlank(resumeJson.getHeader().getPhone())) {
                        resumeJson.getHeader().setPhone(value.toString());
                    }
                }
                case YEARSOFEXPERIENCE -> {
                    if(resumeJson.getHeader().getYearsOfExperience()==0){
                        resumeJson.getHeader().setYearsOfExperience(((Number) value).doubleValue());
                    }
                }

                case ADDRESS -> {
                    if (isBlank(resumeJson.getHeader().getCity())) {
                        resumeJson.getHeader().setCity(value.toString());
                    }
                }

                case SKILLS -> {
                    if (resumeJson.getSkills() == null || resumeJson.getSkills().isEmpty()) {
                        resumeJson.setSkills((List<String>) value);
                    }
                }

                case EDUCATION -> {

                    if (resumeJson.getEducation().isEmpty() && value instanceof List<?> list) {

                        ObjectMapper objectMapper = new ObjectMapper();

                        List<EducationJson> educationList = list.stream()
                                .map(obj -> objectMapper.convertValue(obj, EducationJson.class))
                                .collect(Collectors.toList());

                        resumeJson.getEducation().addAll(educationList);
                    }
                }


                case EXPERIENCE -> {
                    if (resumeJson.getExperience() == null || resumeJson.getExperience().isEmpty()) {
                        resumeJson.setExperience((List<ExperienceJson>) value);
                    }
                }

                case PROJECTS -> {
                    if (resumeJson.getProjects() == null || resumeJson.getProjects().isEmpty()) {
                        resumeJson.setProjects((List<ProjectJson>) value);
                    }
                }

                case CERTIFICATIONS -> {
                    if (resumeJson.getCertifications() == null || resumeJson.getCertifications().isEmpty()) {
                        resumeJson.setCertifications((List<String>) value);
                    }
                }
                case  REDFLAGS -> {
                    if (resumeJson.getRedFlags() == null || resumeJson.getRedFlags().isEmpty()) {
                        resumeJson.setRedFlags((List<String>) value);
                    }
                }
            }
        }
        return  resumeJson;
    }
    private static String jsonKey(Field field) {
        return field.name().toLowerCase();
    }
    private static String jsonTemplateForField(Field field) {

        return switch (field) {

            case NAME, EMAIL, PHONE, ADDRESS ->
                    "\"\"";

            case SKILLS, CERTIFICATIONS ->
                    "[]";

            case EDUCATION ->
                    """
                    [
                    {
                      "institute": "",
                      "degree": "",
                      "duration": "",
                      "score": ""
                    }
                    ]
                    """;

            case PROJECTS ->
                    """
                    [
                      {
                        "title": "",
                        "description": []
                      }
                    ]
                    """;

            case EXPERIENCE ->
                    """
                    [
                      {
                        "company": "",
                        "role": "",
                        "startDate": "",
                        "endDate": "",
                        "responsibilities": []
                      }
                    ]
                    """;
            case REDFLAGS ->
                    "[]";
            case YEARSOFEXPERIENCE -> """
                         return the yearsOfExperience in double (ex : 3.5)
                    """;
        };
    }


    public String buildPromptForFlags(String resumeText) {

        return """
        You are an ATS and recruiter assistant.

        Analyze the resume below and identify potential red flags from a recruiter perspective.

        Rules:
        Detect red flags using the following rules:
        - Missing name → add "Name missing"
        - Missing phone → add "Phone number missing"
        - Missing email → add "Email missing"
        - Missing skills → add "No technical skills mentioned"
        - Less than 1 year experience → add "Very low experience"
        - Education missing → add "Education details missing"
        - Address missing → add "City not mentioned"
        - Gaps > 6 months in experience → add "Employment gaps identified"
        - Inconsistent dates → add "Date inconsistencies found"
        - If any extracted field is empty → add "Incomplete profile information"
        - redFlags → list of issues like missing info, job gaps, no skills, inconsistent dates, etc.

        Return ONLY valid JSON in the following format:
        {
          "redflags": [ "list", "of","strings"]
        }

        ===== RESUME START =====
        %s
        ===== RESUME END =====
        """.formatted(resumeText);
    }

}
