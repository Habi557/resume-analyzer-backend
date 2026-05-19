package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeProjectionDto;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.exceptions.StoredProcedureNotFound;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.projection.ResumeProjection;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ResumeSearchService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeSearchServiceImpl implements ResumeSearchService {
    private final ResumeAnalysis resumeAnalysis;
    private final ConvertingEntityToDtos convertingEntityToDtos;
    private final ResumeRepository resumeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    ResumeSearchServiceImpl(ResumeAnalysis resumeAnalysis,ConvertingEntityToDtos convertingEntityToDtos, ResumeRepository resumeRepository,RedisTemplate redisTemplate){
        this.resumeAnalysis=resumeAnalysis;
        this.convertingEntityToDtos=convertingEntityToDtos;
        this.resumeRepository=resumeRepository;
        this.redisTemplate=redisTemplate;
    }

     @Cacheable(value = "getAllAnalysiedResumesDto", key = "#pageNo + ':' + #pageSize + ':' + #sortBy + ':' + #dir")
    @Override
    public List<ResumeAnalysisDTO> getAllAnalysiedResumes(int pageNo, int pageSize, String sortBy, String dir) {
        if(pageNo<0){
            pageNo=0;
        }
        List<String>  sortingAllowedList = List.of("matchPercentage", "yearsOfExperience", "selectedStatus","analysizedTime");
        if(!sortingAllowedList.contains(sortBy)){
            sortBy="matchPercentage";

        }
        if(sortBy.equals(("yearsOfExperience"))){
            sortBy="resume.yearsOfExperience";
        }
        Sort sorting = (dir.equalsIgnoreCase("ASC"))
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize,sorting);
        Specification<ResumeAnalysisEntity> spec = Specification.where(null);
//        if(sortby!=null){
//            spec = spec.and(ResumeAnalysisSpecification.matchPercentageGreaterThan(sortby));
//        }
        Page<ResumeAnalysisEntity> resumeAnalysisEntities = resumeAnalysis.findAll(spec,pageRequest);
        int totalPages = resumeAnalysisEntities.getTotalPages();
        List<ResumeAnalysisDTO> analysiseResumes = resumeAnalysisEntities.getContent().stream().map(convertingEntityToDtos::convertResumeAnalysisEntityToResumeAnalysisDTO).map(
                resumeAnalysisDTO ->{
                    resumeAnalysisDTO.setTotalPages(totalPages);
                    return resumeAnalysisDTO;
                }
        ).collect(Collectors.toList());
        return analysiseResumes;
    }
    @Cacheable(value = "getAllResumes", key = "#pageNo + ':' + #pageSize")
    @Override
    public List<ResumeProjectionDto> getAllResumes(int pageNo, int pagesize) {
//        String cacheKey = "getAllResumes" + ":" + pageNo + ":" + pagesize;
//        List<ResumeProjection> cachedResumes = (List<ResumeProjection>) redisTemplate.opsForValue().get(cacheKey);
//        if (cachedResumes != null && cachedResumes.size() > 0) {
//            return cachedResumes;
//        }
        PageRequest pageRequest = PageRequest.of(pageNo, pagesize);
        try{
        Page<ResumeProjection> allResumes = resumeRepository.findAllResumes(pageRequest);
            return allResumes.getContent()
                    .stream()
                    .map(r -> new ResumeProjectionDto(
                            r.getId(),
                            r.getOriginalFileName(),
                            r.getName()
                    ))
                    .collect(Collectors.toList());
        }catch (InvalidDataAccessResourceUsageException e){
           throw new StoredProcedureNotFound("Stored Procedure unavailable:");
        }
      //  redisTemplate.opsForValue().set(cacheKey, allResumes.getContent());

    }

    //    @Cacheable(
//            value = "resumesBySkillName",
//            key = "#skillName + ':' + #currentPage + ':' + #pageSize"
//    )
    @Override
    public List<ResumeAnalysisDTO> findResumesBySkillName(String skillName, int currentPage, int pageSize) {
        System.out.println("DB HIT for skill = " + skillName);
//        Page<Long> pageIds= resumeRepository.findResumeIdsBySkill(skillName, PageRequest.of(currentPage, pageSize));
//        List<Resume> resumesWithSkills = new ArrayList<Resume>();
//        if(pageIds.isEmpty()){
//            return  resumesWithSkills;
//        }
//        resumesWithSkills = resumeRepository.findResumesWithSkills(pageIds.toList());
//
//
//        return resumesWithSkills;

        // Get paginated analysis entities directly (sorted by latest first)
        Page<ResumeAnalysisEntity> analysisPage = resumeAnalysis.findByResume_Skills_NameAndOrderByLatest(
                skillName,
                PageRequest.of(currentPage, pageSize)
        );

        return analysisPage.getContent()
                .stream()
                .map(ra -> {
                    Resume resume = ra.getResume();
                    ResumeTempDto resumeTempDto = convertingEntityToDtos.convertResumeDto(resume);
                    return new ResumeAnalysisDTO(
                            ra.getId(),
                            ra.getMatchPercentage(),
                            ra.getSuggestions(),
                            ra.getConclusion(),
                            ra.getAnalysizedTime(),
                            ra.getTopMatchingSkills(),
                            resumeTempDto,
                            ra.getInterviewDate(),
                            ra.getInterviewTime(),
                            ra.getInterviewMode(),
                            ra.getSelectedStatus()
                    );
                })
                .collect(Collectors.toList());
    }

}
