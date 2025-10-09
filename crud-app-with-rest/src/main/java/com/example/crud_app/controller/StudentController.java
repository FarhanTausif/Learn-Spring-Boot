package com.example.crud_app.controller;

import com.example.crud_app.dto.StudentSummaryDTO;
import com.example.crud_app.dto.StudentWithCoursesDTO;
import com.example.crud_app.model.Student;
import com.example.crud_app.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Basic CRUD Operations

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        // Using Optional<> as mentor suggested - demonstrates power of derived queries
        Optional<Student> studentOpt = studentService.getStudentById(id);
        return studentOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student) {
        // Check if email already exists using derived query method
        if (studentService.existsByEmail(student.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already exists");
            error.put("field", "email");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Student savedStudent = studentService.saveStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody Student studentDetails) {
        // Using Optional<> to check existence first
        Optional<Student> existingStudentOpt = studentService.getStudentById(id);
        if (existingStudentOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Student not found");
            error.put("studentId", id.toString());
            return ResponseEntity.notFound().build();
        }

        // Check email uniqueness if email is being changed
        Student existingStudent = existingStudentOpt.get();
        if (!existingStudent.getEmail().equals(studentDetails.getEmail()) &&
            studentService.existsByEmail(studentDetails.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already exists");
            error.put("field", "email");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
        // Using Optional<> to verify existence before deletion
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            studentService.deleteStudent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student and associated courses deleted successfully");
            response.put("deletedStudentId", id.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DTO-based endpoints (subset of attributes as mentor suggested)

    @GetMapping("/summary")
    public ResponseEntity<List<StudentSummaryDTO>> getAllStudentSummaries() {
        List<StudentSummaryDTO> summaries = studentService.getAllStudentSummaries();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/with-courses")
    public ResponseEntity<List<StudentWithCoursesDTO>> getStudentsWithCourseStats() {
        List<StudentWithCoursesDTO> stats = studentService.getStudentsWithCourseStats();
        return ResponseEntity.ok(stats);
    }

    // Advanced endpoints using derived query methods (power of JPQL as mentor mentioned)

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String query) {
        // Uses derived query method: findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
        List<Student> students = studentService.searchStudents(query);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/older-than/{age}")
    public ResponseEntity<List<Student>> getStudentsOlderThan(@PathVariable Integer age) {
        // Uses derived query method: findByAgeGreaterThan
        List<Student> students = studentService.getStudentsOlderThan(age);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/age-range")
    public ResponseEntity<List<Student>> getStudentsByAgeRange(
            @RequestParam Integer minAge, @RequestParam Integer maxAge) {
        // Uses derived query method: findByAgeBetween
        List<Student> students = studentService.getStudentsByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/without-courses")
    public ResponseEntity<List<StudentWithCoursesDTO>> getStudentsWithoutCourses() {
        // Uses DTO for subset of attributes with course statistics
        List<StudentWithCoursesDTO> students = studentService.getStudentsWithoutCourses();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/minimum-courses/{count}")
    public ResponseEntity<List<StudentWithCoursesDTO>> getStudentsWithMinimumCourses(@PathVariable Integer count) {
        // Uses DTO for subset of attributes with course statistics
        List<StudentWithCoursesDTO> students = studentService.getStudentsWithMinimumCourses(count);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/top-by-age")
    public ResponseEntity<List<Student>> getTopStudentsByAge() {
        // Uses derived query method: findTop5ByOrderByAgeDesc
        List<Student> students = studentService.getTopStudentsByAge();
        return ResponseEntity.ok(students);
    }

    // Utility endpoints

    @GetMapping("/count-with-courses")
    public ResponseEntity<Map<String, Long>> countStudentsWithCourses() {
        Long count = studentService.countStudentsWithCourses();
        Map<String, Long> response = new HashMap<>();
        response.put("studentsWithCoursesCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable String email) {
        // Uses derived query method: existsByEmail
        boolean exists = studentService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseEntity<?> findStudentByEmail(@PathVariable String email) {
        // Demonstrates Optional<> usage with derived query methods
        Optional<Student> studentOpt = studentService.findByEmail(email);
        if (studentOpt.isPresent()) {
            return ResponseEntity.ok(studentOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No student found with email: " + email);
            return ResponseEntity.notFound().build();
        }
    }
}
