package com.example.crud_app.controller;

import com.example.crud_app.dto.CourseDTO;
import com.example.crud_app.dto.StudentSummaryDTO;
import com.example.crud_app.dto.StudentWithCoursesDTO;
import com.example.crud_app.model.Course;
import com.example.crud_app.model.Student;
import com.example.crud_app.service.CourseService;
import com.example.crud_app.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jpql-demo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JPQLTestController {

    private final StudentService studentService;
    private final CourseService courseService;

    // Demonstrate the power of derived query methods (as mentor suggested)
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> demonstrateJPQLPower() {
        Map<String, Object> results = new HashMap<>();

        // 1. Using derived query methods to find students by age range
        results.put("studentsAge18to25",
            studentService.getStudentsByAgeRange(18, 25));

        // 2. Using derived query methods to find students older than specific age
        results.put("studentsOlderThan20",
            studentService.getStudentsOlderThan(20));

        // 3. Using DTO to show subset of attributes (as mentor suggested)
        results.put("studentSummaries",
            studentService.getAllStudentSummaries());

        // 4. Demonstrating course-related derived queries
        results.put("coursesWithMinCredits",
            courseService.getCoursesByMinCredits(3));

        // 5. Unassigned courses using derived query
        results.put("unassignedCourses",
            courseService.getUnassignedCourses());

        // 6. Students with course statistics using DTOs
        results.put("studentsWithCourseStats",
            studentService.getStudentsWithCourseStats());

        // 7. Top students by age using derived query
        results.put("topStudentsByAge",
            studentService.getTopStudentsByAge());

        // 8. Count statistics using derived queries
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalStudentsWithCourses", studentService.countStudentsWithCourses());
        statistics.put("totalUnassignedCourses", courseService.countUnassignedCourses());
        results.put("statistics", statistics);

        return ResponseEntity.ok(results);
    }

    // Demonstrate Optional<> usage with derived query methods
    @GetMapping("/find-student-by-email/{email}")
    public ResponseEntity<?> findStudentByEmailDemo(@PathVariable String email) {
        // Using Optional<> as mentor suggested - shows power of derived queries
        Optional<Student> studentOpt = studentService.findByEmail(email);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("student", student);
            result.put("studentCourses", courseService.getCoursesByStudentId(student.getStudentId()));
            result.put("totalCredits", courseService.getTotalCreditsByStudentId(student.getStudentId()));
            return ResponseEntity.ok(result);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Student not found with email: " + email);
            return ResponseEntity.notFound().build();
        }
    }

    // Demonstrate Optional<> usage with course queries
    @GetMapping("/find-course-by-title/{title}")
    public ResponseEntity<?> findCourseByTitleDemo(@PathVariable String title) {
        // Using Optional<> as mentor suggested
        Optional<Course> courseOpt = courseService.findByTitle(title);

        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("course", course);

            // If course has a student, get student info
            if (course.getStudentId() != null) {
                Optional<Student> studentOpt = studentService.getStudentById(course.getStudentId());
                studentOpt.ifPresent(student -> result.put("assignedStudent", student));
            }

            return ResponseEntity.ok(result);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course not found with title: " + title);
            return ResponseEntity.notFound().build();
        }
    }

    // Demonstrate search capabilities using derived queries
    @GetMapping("/search-demo")
    public ResponseEntity<Map<String, Object>> searchDemo(@RequestParam String query) {
        Map<String, Object> results = new HashMap<>();

        // Search using derived query methods
        results.put("studentsFound", studentService.searchStudents(query));
        results.put("coursesFound", courseService.searchCoursesByTitle(query));

        return ResponseEntity.ok(results);
    }

    // Demonstrate DTO usage for subset of attributes
    @GetMapping("/dto-demo")
    public ResponseEntity<Map<String, Object>> dtoDemo() {
        Map<String, Object> results = new HashMap<>();

        // Using DTOs to return subset of attributes (as mentor suggested)
        results.put("studentSummaries", studentService.getAllStudentSummaries());
        results.put("studentsWithCourseStats", studentService.getStudentsWithCourseStats());
        results.put("coursesAsDTO", courseService.getAllCoursesAsDTO());

        return ResponseEntity.ok(results);
    }

    // Demonstrate filtering using derived queries
    @GetMapping("/filtering-demo")
    public ResponseEntity<Map<String, Object>> filteringDemo() {
        Map<String, Object> results = new HashMap<>();

        // Various filtering examples using derived queries
        results.put("studentsWithoutCourses", studentService.getStudentsWithoutCourses());
        results.put("studentsWithMinimum2Courses", studentService.getStudentsWithMinimumCourses(2));
        results.put("coursesWithMinCredits", courseService.getCoursesByMinCredits(3));
        results.put("coursesInRange2to4Credits", courseService.getCoursesByCreditsRange(2, 4));
        results.put("topCoursesByCredits", courseService.getTopCoursesByCredits());

        return ResponseEntity.ok(results);
    }

    // Demonstrate counting and existence checks using derived queries
    @GetMapping("/count-demo/{studentId}")
    public ResponseEntity<Map<String, Object>> countDemo(@PathVariable Long studentId) {
        Map<String, Object> results = new HashMap<>();

        // Using Optional<> to check if student exists first
        Optional<Student> studentOpt = studentService.getStudentById(studentId);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            results.put("student", student);
            results.put("courseCount", courseService.countCoursesByStudentId(studentId));
            results.put("totalCredits", courseService.getTotalCreditsByStudentId(studentId));
            results.put("studentCourses", courseService.getCoursesByStudentId(studentId));
        } else {
            results.put("error", "Student not found with ID: " + studentId);
        }

        return ResponseEntity.ok(results);
    }

    // Demonstrate validation using derived queries
    @GetMapping("/validation-demo")
    public ResponseEntity<Map<String, Object>> validationDemo(
            @RequestParam String email,
            @RequestParam String courseTitle) {

        Map<String, Object> results = new HashMap<>();

        // Using derived query methods for validation
        results.put("emailExists", studentService.existsByEmail(email));
        results.put("courseTitleExists", courseService.existsByTitle(courseTitle));

        // If email exists, get the student
        if (studentService.existsByEmail(email)) {
            Optional<Student> studentOpt = studentService.findByEmail(email);
            studentOpt.ifPresent(student -> results.put("existingStudent", student));
        }

        // If course title exists, get the course
        if (courseService.existsByTitle(courseTitle)) {
            Optional<Course> courseOpt = courseService.findByTitle(courseTitle);
            courseOpt.ifPresent(course -> results.put("existingCourse", course));
        }

        return ResponseEntity.ok(results);
    }
}
