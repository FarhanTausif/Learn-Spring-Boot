package com.example.crud_app.controller;

import com.example.crud_app.dto.CourseDTO;
import com.example.crud_app.model.Course;
import com.example.crud_app.model.Student;
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

    // GET /api/courses - Get all courses with optional filtering
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer minCredits,
            @RequestParam(required = false) Integer maxCredits,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "false") Boolean unassigned,
            @RequestParam(required = false, defaultValue = "false") Boolean dto) {

        List<Course> courses;

        // Apply filters based on query parameters
        if (studentId != null) {
            courses = courseService.getCoursesByStudentId(studentId);
        } else if (unassigned) {
            courses = courseService.getUnassignedCourses();
        } else if (title != null) {
            courses = courseService.searchCoursesByTitle(title);
        } else if (minCredits != null && maxCredits != null) {
            courses = courseService.getCoursesByCreditsRange(minCredits, maxCredits);
        } else if (minCredits != null) {
            courses = courseService.getCoursesByMinCredits(minCredits);
        } else {
            courses = courseService.getAllCourses();
        }

        return ResponseEntity.ok(courses);
    }

    // GET /api/courses/dto - Get courses as DTO
    @GetMapping("/dto")
    public ResponseEntity<List<CourseDTO>> getAllCoursesAsDTO() {
        List<CourseDTO> courseDTOs = courseService.getAllCoursesAsDTO();
        return ResponseEntity.ok(courseDTOs);
    }

    // GET /api/courses/{id} - Get course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> courseOpt = courseService.getCourseById(id);
        return courseOpt.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/courses/{id}/dto - Get course DTO by ID
    @GetMapping("/{id}/dto")
    public ResponseEntity<CourseDTO> getCourseDTOById(@PathVariable Long id) {
        Optional<CourseDTO> courseDTOOpt = courseService.getCourseDTOById(id);
        return courseDTOOpt.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/courses - Create new course
    @PostMapping
    public ResponseEntity<?> createCourse(
            @Valid @RequestBody Course course,
            @RequestParam(required = false) Long studentId) {

        // Check if title already exists
        if (courseService.existsByTitle(course.getTitle())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Course with this title already exists");
            error.put("field", "title");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        try {
            Course savedCourse;
            if (studentId != null) {
                savedCourse = courseService.createCourseForStudent(studentId, course);
            } else {
                savedCourse = courseService.saveCourse(course);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // PUT /api/courses/{id} - Update course
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody Course courseDetails) {
        Optional<Course> existingCourseOpt = courseService.getCourseById(id);
        if (existingCourseOpt.isEmpty()) {
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

    // DELETE /api/courses/{id} - Delete course
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
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

    // GET /api/courses/stats - Get course statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCourseStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", courseService.getAllCourses().size());
        stats.put("unassignedCoursesCount", courseService.countUnassignedCourses());
        stats.put("topCoursesByCredits", courseService.getTopCoursesByCredits());
        return ResponseEntity.ok(stats);
    }

    // GET /api/courses/{courseId}/student - Get student taking a specific course
    @GetMapping("/{courseId}/student")
    public ResponseEntity<?> getStudentForCourse(@PathVariable Long courseId) {
        try {
            Student student = courseService.getStudentForCourse(courseId);
            if (student == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Course is not assigned to any student");
                response.put("courseId", courseId.toString());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // PUT /api/courses/{courseId}/student/{studentId} - Reassign course to different student
    @PutMapping("/{courseId}/student/{studentId}")
    public ResponseEntity<?> reassignCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        try {
            courseService.reassignCourse(courseId, studentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course reassigned successfully");
            response.put("courseId", courseId.toString());
            response.put("newStudentId", studentId.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // DELETE /api/courses/{courseId}/student - Unassign course from any student
    @DeleteMapping("/{courseId}/student")
    public ResponseEntity<?> unassignCourse(@PathVariable Long courseId) {
        try {
            courseService.unassignCourse(courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course unassigned successfully");
            response.put("courseId", courseId.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // GET /api/courses/students - Get all students who are taking courses
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getStudentsTakingCourses() {
        List<Student> students = courseService.getStudentsTakingCourses();
        return ResponseEntity.ok(students);
    }
}
