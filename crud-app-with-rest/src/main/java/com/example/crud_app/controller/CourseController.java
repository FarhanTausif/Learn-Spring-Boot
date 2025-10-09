package com.example.crud_app.controller;

import com.example.crud_app.dto.CourseDTO;
import com.example.crud_app.model.Course;
import com.example.crud_app.service.CourseService;
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
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // Basic CRUD Operations

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        // Using Optional<> as mentor suggested - demonstrates power of derived queries
        Optional<Course> courseOpt = courseService.getCourseById(id);
        return courseOpt.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody Course course) {
        // Check if title already exists using derived query method
        if (courseService.existsByTitle(course.getTitle())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course with this title already exists");
            error.put("field", "title");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Course savedCourse = courseService.saveCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create course: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/student/{studentId}")
    public ResponseEntity<?> createCourseForStudent(@PathVariable Long studentId,
                                                   @Valid @RequestBody Course course) {
        // Check if title already exists using derived query method
        if (courseService.existsByTitle(course.getTitle())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course with this title already exists");
            error.put("field", "title");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Course savedCourse = courseService.createCourseForStudent(studentId, course);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create course: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody Course courseDetails) {
        // Using Optional<> to check existence first
        Optional<Course> existingCourseOpt = courseService.getCourseById(id);
        if (existingCourseOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course not found");
            error.put("courseId", id.toString());
            return ResponseEntity.notFound().build();
        }

        // Check title uniqueness if title is being changed
        Course existingCourse = existingCourseOpt.get();
        if (!existingCourse.getTitle().equals(courseDetails.getTitle()) &&
            courseService.existsByTitle(courseDetails.getTitle())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course with this title already exists");
            error.put("field", "title");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Course updatedCourse = courseService.updateCourse(id, courseDetails);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update course: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        // Using Optional<> to verify existence before deletion
        Optional<Course> courseOpt = courseService.getCourseById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            courseService.deleteCourseById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course deleted successfully");
            response.put("deletedCourseId", id.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete course: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DTO-based endpoints (subset of attributes as mentor suggested)

    @GetMapping("/dto")
    public ResponseEntity<List<CourseDTO>> getAllCoursesAsDTO() {
        List<CourseDTO> courseDTOs = courseService.getAllCoursesAsDTO();
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<CourseDTO> getCourseDTOById(@PathVariable Long id) {
        Optional<CourseDTO> courseDTOOpt = courseService.getCourseDTOById(id);
        return courseDTOOpt.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // Advanced endpoints using derived query methods (power of JPQL as mentor mentioned)

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Course>> getCoursesByStudentId(@PathVariable Long studentId) {
        // Uses derived query method: findByStudentId
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/min-credits/{credits}")
    public ResponseEntity<List<Course>> getCoursesByMinCredits(@PathVariable Integer credits) {
        // Uses derived query method: findByCreditsGreaterThanEqualOrderByCreditsDesc
        List<Course> courses = courseService.getCoursesByMinCredits(credits);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/credits/{credits}")
    public ResponseEntity<List<Course>> getCoursesByExactCredits(@PathVariable Integer credits) {
        // Uses derived query method: findByCredits
        List<Course> courses = courseService.getCoursesByCredits(credits);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/credits-range")
    public ResponseEntity<List<Course>> getCoursesByCreditsRange(
            @RequestParam Integer minCredits, @RequestParam Integer maxCredits) {
        // Uses derived query method: findByCreditsBetween
        List<Course> courses = courseService.getCoursesByCreditsRange(minCredits, maxCredits);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Course>> getUnassignedCourses() {
        // Uses derived query method: findByStudentIdIsNull
        List<Course> courses = courseService.getUnassignedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<Course>> getAssignedCourses() {
        // Uses derived query method: findByStudentIdIsNotNull
        List<Course> courses = courseService.getAssignedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCoursesByTitle(@RequestParam String title) {
        // Uses derived query method: findByTitleContainingIgnoreCase
        List<Course> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/top-credits")
    public ResponseEntity<List<Course>> getTopCoursesByCredits() {
        // Uses derived query method: findTop5ByOrderByCreditsDesc
        List<Course> courses = courseService.getTopCoursesByCredits();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/find-by-title/{title}")
    public ResponseEntity<?> findCourseByTitle(@PathVariable String title) {
        // Demonstrates Optional<> usage with derived query methods
        Optional<Course> courseOpt = courseService.findByTitle(title);
        if (courseOpt.isPresent()) {
            return ResponseEntity.ok(courseOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No course found with title: " + title);
            return ResponseEntity.notFound().build();
        }
    }

    // Statistical endpoints using derived query methods

    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<Map<String, Long>> countCoursesByStudent(@PathVariable Long studentId) {
        // Uses derived query method: countByStudentId
        Long count = courseService.countCoursesByStudentId(studentId);
        Map<String, Long> response = new HashMap<>();
        response.put("studentId", studentId);
        response.put("courseCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}/total-credits")
    public ResponseEntity<Map<String, Object>> getTotalCreditsForStudent(@PathVariable Long studentId) {
        Integer totalCredits = courseService.getTotalCreditsByStudentId(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("studentId", studentId);
        response.put("totalCredits", totalCredits);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/credits/{credits}/count")
    public ResponseEntity<Map<String, Long>> countCoursesByCredits(@PathVariable Integer credits) {
        // Uses derived query method: countByCredits
        Long count = courseService.countCoursesByCredits(credits);
        Map<String, Long> response = new HashMap<>();
        response.put("credits", Long.valueOf(credits));
        response.put("courseCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned/count")
    public ResponseEntity<Map<String, Long>> countUnassignedCourses() {
        // Uses derived query method: countByStudentIdIsNull
        Long count = courseService.countUnassignedCourses();
        Map<String, Long> response = new HashMap<>();
        response.put("unassignedCoursesCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists-title/{title}")
    public ResponseEntity<Map<String, Boolean>> checkTitleExists(@PathVariable String title) {
        // Uses derived query method: existsByTitle
        boolean exists = courseService.existsByTitle(title);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
