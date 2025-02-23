/****** Script for SelectTopNRows command from SSMS  ******/
SELECT cgo.EducationOrganizationId
      ,cgo.IdentityCourseCode
      --,cgo.GradesOfferedTypeId
      ,glt.CodeValue as GradeLevel
  FROM EdFi.edfi.CourseGradesOffered cgo
  LEFT JOIN EdFi.edfi.GradeLevelType glt ON cgo.GradesOfferedTypeId = glt.GradeLevelTypeId
  ORDER BY cgo.EducationOrganizationId, cgo.IdentityCourseCode
