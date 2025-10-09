package com.example.crud_app.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithCoursesDTO {
    private Long studentId;
    private String name;
    private String email;
    private Integer age;
    private Integer totalCourses;
    private Integer totalCredits;
}
