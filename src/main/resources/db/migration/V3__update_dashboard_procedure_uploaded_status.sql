DROP PROCEDURE IF EXISTS get_dashboard_details;

CREATE PROCEDURE get_dashboard_details()
BEGIN

    SELECT
        COUNT(*) AS totalResumes,

        SUM(CASE WHEN scan_all_resumes = 1 THEN 1 ELSE 0 END) AS totalAnalysed,

        SUM(CASE WHEN scan_all_resumes = 0 THEN 1 ELSE 0 END) AS totalNotAnalysed,

        MAX(rad.candidatesScreened) AS candidatesScreened,
        MAX(rad.bestMatch) AS bestMatch,

        ROUND(IFNULL(AVG(
            CASE
                WHEN years_of_experience <> 0
                THEN years_of_experience
            END
        ),0)) AS averageExperience

    FROM resume r
    CROSS JOIN (
        SELECT
            COUNT(*) AS candidatesScreened,
            MAX(match_percentage) AS bestMatch
        FROM resume_analysis_data
    ) rad
    where r.status = 'UPLOADED';

END;