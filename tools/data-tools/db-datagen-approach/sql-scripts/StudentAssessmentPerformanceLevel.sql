/****** Script for SelectTopNRows command from SSMS  ******/
SELECT sapl.StudentUSI
      ,sapl.AssessmentTitle
      ,sapl.AcademicSubjectTypeId
      ,sapl.AssessedGradeLevelTypeId
      ,sapl.Version
      ,sapl.AdministrationDate
      ,pld.Description as PerformanceLevelDescriptor
      ,sapl.PerformanceLevelMet
  FROM EdFi.edfi.StudentAssessmentPerformanceLevel sapl
  LEFT JOIN EdFi.edfi.PerformanceLevelDescriptor pld ON sapl.PerformanceLevelDescriptorId = pld.PerformanceLevelDescriptorId
  ORDER BY sapl.StudentUSI, sapl.AssessmentTitle, sapl.AcademicSubjectTypeId, sapl.AssessedGradeLevelTypeId, sapl.Version, sapl.AdministrationDate
