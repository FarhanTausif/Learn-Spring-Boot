package com.example.crud_app.controller;

import com.example.crud_app.dto.StudentSummaryDTO;
import com.example.crud_app.dto.StudentWithCoursesDTO;
import com.example.crud_app.model.Student;
import com.example.crud_app.model.Course;
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

    // GET /api/students - Get all students with optional filtering
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, defaultValue = "false") Boolean withoutCourses,
            @RequestParam(required = false) Integer minCourses) {

        List<Student> students;

        // Apply filters based on query parameters
        if (email != null) {
            Optional<Student> studentOpt = studentService.findByEmail(email);
            students = studentOpt.map(List::of).orElse(List.of());
        } else if (search != null) {
            students = studentService.searchStudents(search);
        } else if (minAge != null && maxAge != null) {
            students = studentService.getStudentsByAgeRange(minAge, maxAge);
        } else if (minAge != null) {
            students = studentService.getStudentsOlderThan(minAge);
        } else {
            students = studentService.getAllStudents();
        }

        return ResponseEntity.ok(students);
    }

    // GET /api/students/dto - Get students as DTO with course information
    @GetMapping("/dto")
    public ResponseEntity<List<StudentWithCoursesDTO>> getStudentsWithCourseStats(
            @RequestParam(required = false, defaultValue = "false") Boolean withoutCourses,
            @RequestParam(required = false) Integer minCourses) {

        List<StudentWithCoursesDTO> students;

        if (withoutCourses) {
            students = studentService.getStudentsWithoutCourses();
        } else if (minCourses != null) {
            students = studentService.getStudentsWithMinimumCourses(minCourses);
        } else {
            students = studentService.getStudentsWithCourseStats();
        }

        return ResponseEntity.ok(students);
    }

    // GET /api/students/summary - Get student summaries
    @GetMapping("/summary")
    public ResponseEntity<List<StudentSummaryDTO>> getAllStudentSummaries() {
        List<StudentSummaryDTO> summaries = studentService.getAllStudentSummaries();
        return ResponseEntity.ok(summaries);
    }

    // GET /api/students/{id} - Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        return studentOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/students - Create new student
    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student) {
        // Check if email already exists
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

    // PUT /api/students/{id} - Update student
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody Student studentDetails) {
        Optional<Student> existingStudentOpt = studentService.getStudentById(id);
        if (existingStudentOpt.isEmpty()) {
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

    // DELETE /api/students/{id} - Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
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

    // GET /api/students/stats - Get student statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStudentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<Student> allStudents = studentService.getAllStudents();

        stats.put("totalStudents", allStudents.size());
        stats.put("studentsWithCoursesCount", studentService.countStudentsWithCourses());
        stats.put("studentsWithoutCoursesCount", studentService.getStudentsWithoutCourses().size());
        stats.put("topStudentsByAge", studentService.getTopStudentsByAge());

        // Calculate average age
        double avgAge = allStudents.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
        stats.put("averageAge", Math.round(avgAge * 10.0) / 10.0);

        return ResponseEntity.ok(stats);
    }

    // POST /api/students/{studentId}/courses/{courseId} - Assign course to student
    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<?> assignCourseToStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            studentService.assignCourseToStudent(studentId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course assigned to student successfully");
            response.put("studentId", studentId.toString());
            response.put("courseId", courseId.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // DELETE /api/students/{studentId}/courses/{courseId} - Remove course from student
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<?> removeCourseFromStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            studentService.removeCourseFromStudent(studentId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course removed from student successfully");
            response.put("studentId", studentId.toString());
            response.put("courseId", courseId.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // GET /api/students/{studentId}/courses - Get all courses for a specific student
    @GetMapping("/{studentId}/courses")
    public ResponseEntity<?> getStudentCourses(@PathVariable Long studentId) {
        try {
            List<Course> courses = studentService.getCoursesForStudent(studentId);
            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // GET /api/students/{studentId}/total-credits - Get total credits for a student
    @GetMapping("/{studentId}/total-credits")
    public ResponseEntity<?> getStudentTotalCredits(@PathVariable Long studentId) {
        try {
            int totalCredits = studentService.getTotalCreditsForStudent(studentId);
            Map<String, Object> response = new HashMap<>();
            response.put("studentId", studentId);
            response.put("totalCredits", totalCredits);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
