package com.example.crud_app.controller;

import com.example.crud_app.service.CourseService;
import com.example.crud_app.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JPQLTestController {

    private final StudentService studentService;
    private final CourseService courseService;

    // Single endpoint to demonstrate JPQL capabilities
    @GetMapping("/jpql")
    public ResponseEntity<Map<String, Object>> demonstrateJPQLCapabilities() {
        Map<String, Object> results = new HashMap<>();

        // Demonstrate derived query methods
        results.put("studentsAge18to25", studentService.getStudentsByAgeRange(18, 25));
        results.put("studentsOlderThan20", studentService.getStudentsOlderThan(20));
        results.put("studentSummaries", studentService.getAllStudentSummaries());
        results.put("coursesWithMinCredits", courseService.getCoursesByMinCredits(3));
        results.put("unassignedCourses", courseService.getUnassignedCourses());
        results.put("studentsWithCourseStats", studentService.getStudentsWithCourseStats());
        results.put("topStudentsByAge", studentService.getTopStudentsByAge());

        // Statistics using derived queries
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalStudentsWithCourses", studentService.countStudentsWithCourses());
        statistics.put("totalUnassignedCourses", courseService.countUnassignedCourses());
        results.put("statistics", statistics);

        return ResponseEntity.ok(results);
    }
}
