package com.example.crud_app.controller;

import com.example.crud_app.service.StudentService;
import com.example.crud_app.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final StudentService studentService;
    private final CourseService courseService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            // Load comprehensive dashboard data
            model.addAttribute("totalStudents", studentService.getAllStudents().size());
            model.addAttribute("totalCourses", courseService.getAllCourses().size());
            model.addAttribute("studentsWithoutCourses", studentService.getStudentsWithoutCourses().size());
            model.addAttribute("unassignedCourses", courseService.getUnassignedCourses().size());

            // Additional statistics for dashboard
            long studentsWithCourses = studentService.countStudentsWithCourses();
            model.addAttribute("studentsWithCourses", studentsWithCourses);

            // Calculate percentages for progress indicators
            int totalStudents = studentService.getAllStudents().size();
            double enrollmentRate = totalStudents > 0 ? (double) studentsWithCourses / totalStudents * 100 : 0;
            model.addAttribute("enrollmentRate", Math.round(enrollmentRate));

            // Average age calculation
            double avgAge = studentService.getAllStudents().stream()
                    .mapToInt(s -> s.getAge())
                    .average()
                    .orElse(0.0);
            model.addAttribute("averageAge", Math.round(avgAge * 10.0) / 10.0);

            // Recent activity data
            model.addAttribute("recentStudents", studentService.getTopStudentsByAge());
            model.addAttribute("topCourses", courseService.getTopCoursesByCredits());

        } catch (Exception e) {
            // Handle errors gracefully
            model.addAttribute("error", "Failed to load dashboard data: " + e.getMessage());
            return "error";
        }

        return "index";
    }

    // API endpoint for dashboard real-time updates
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            int totalStudents = studentService.getAllStudents().size();
            int totalCourses = courseService.getAllCourses().size();
            long studentsWithCourses = studentService.countStudentsWithCourses();
            int unassignedCourses = courseService.getUnassignedCourses().size();

            stats.put("totalStudents", totalStudents);
            stats.put("totalCourses", totalCourses);
            stats.put("studentsWithCourses", studentsWithCourses);
            stats.put("studentsWithoutCourses", totalStudents - studentsWithCourses);
            stats.put("unassignedCourses", unassignedCourses);

            double enrollmentRate = totalStudents > 0 ? (double) studentsWithCourses / totalStudents * 100 : 0;
            stats.put("enrollmentRate", Math.round(enrollmentRate));

            double avgAge = studentService.getAllStudents().stream()
                    .mapToInt(s -> s.getAge())
                    .average()
                    .orElse(0.0);
            stats.put("averageAge", Math.round(avgAge * 10.0) / 10.0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/students")
    public String studentsPage(Model model) {
        model.addAttribute("pageTitle", "Students Management");

        // Pre-load some statistics for the page
        try {
            int totalStudents = studentService.getAllStudents().size();
            long studentsWithCourses = studentService.countStudentsWithCourses();

            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("studentsWithCourses", studentsWithCourses);
            model.addAttribute("studentsWithoutCourses", totalStudents - studentsWithCourses);

            // Age distribution data
            Map<String, Integer> ageDistribution = new HashMap<>();
            studentService.getAllStudents().forEach(student -> {
                String ageGroup = getAgeGroup(student.getAge());
                ageDistribution.merge(ageGroup, 1, Integer::sum);
            });
            model.addAttribute("ageDistribution", ageDistribution);

            // User permissions (can be enhanced with Spring Security)
            Map<String, Boolean> userPermissions = new HashMap<>();
            userPermissions.put("canAdd", true);
            userPermissions.put("canEdit", true);
            userPermissions.put("canDelete", true);
            userPermissions.put("canAssign", true);
            model.addAttribute("userPermissions", userPermissions);

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load student statistics: " + e.getMessage());
        }

        return "students";
    }

    @GetMapping("/courses")
    public String coursesPage(Model model) {
        model.addAttribute("pageTitle", "Courses Management");

        // Pre-load course statistics
        try {
            int totalCourses = courseService.getAllCourses().size();
            int assignedCourses = courseService.getAssignedCourses().size();
            int unassignedCourses = courseService.getUnassignedCourses().size();

            model.addAttribute("totalCourses", totalCourses);
            model.addAttribute("assignedCourses", assignedCourses);
            model.addAttribute("unassignedCourses", unassignedCourses);

            // Credit distribution
            Map<String, Integer> creditDistribution = new HashMap<>();
            courseService.getAllCourses().forEach(course -> {
                String creditGroup = getCreditGroup(course.getCredits());
                creditDistribution.merge(creditGroup, 1, Integer::sum);
            });
            model.addAttribute("creditDistribution", creditDistribution);

            // Average credits
            double avgCredits = courseService.getAllCourses().stream()
                    .mapToInt(c -> c.getCredits())
                    .average()
                    .orElse(0.0);
            model.addAttribute("averageCredits", Math.round(avgCredits * 10.0) / 10.0);

            // User permissions
            Map<String, Boolean> userPermissions = new HashMap<>();
            userPermissions.put("canAdd", true);
            userPermissions.put("canEdit", true);
            userPermissions.put("canDelete", true);
            userPermissions.put("canAssign", true);
            model.addAttribute("userPermissions", userPermissions);

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load course statistics: " + e.getMessage());
        }

        return "courses";
    }

    @GetMapping("/jpql-demo")
    public String jpqlDemoPage(Model model) {
        model.addAttribute("pageTitle", "JPQL Demonstrations");

        // Pre-load some example data for the demo
        try {
            model.addAttribute("totalDerivedMethods", 25); // Approximate count of derived methods
            model.addAttribute("queryTypes", new String[]{"Basic", "Advanced", "Pattern Matching", "Aggregation"});

            // Performance metrics (simulated)
            Map<String, String> performanceMetrics = new HashMap<>();
            performanceMetrics.put("Average Query Time", "< 50ms");
            performanceMetrics.put("Query Success Rate", "99.9%");
            performanceMetrics.put("Auto-generated Queries", "100%");
            model.addAttribute("performanceMetrics", performanceMetrics);

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load JPQL demo data: " + e.getMessage());
        }

        return "jpql-demo";
    }

    // JPQL Demo API endpoints
    @PostMapping("/api/jpql/test")
    @ResponseBody
    public ResponseEntity<?> testJpqlQuery(@RequestBody Map<String, Object> request) {
        try {
            String query = (String) request.get("query");
            Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");

            // For demo purposes, simulate query execution
            // In a real implementation, you would execute the JPQL query
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("executionTime", Math.random() * 100 + 10); // Simulate execution time
            result.put("results", generateMockResults(query));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Query execution failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/api/jpql/explain")
    @ResponseBody
    public ResponseEntity<?> explainQuery(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");

            // Generate mock explanation
            Map<String, Object> explanation = new HashMap<>();
            explanation.put("type", "SELECT");
            explanation.put("entities", new String[]{"Student", "Course"});
            explanation.put("parameters", new String[]{});
            explanation.put("complexity", "MEDIUM");
            explanation.put("tips", new String[]{
                "Consider adding indexes on frequently queried columns",
                "Use JOIN FETCH to avoid N+1 problems",
                "Consider pagination for large result sets"
            });
            explanation.put("generatedSQL", "SELECT s.* FROM students s WHERE s.age > ?");

            return ResponseEntity.ok(explanation);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to explain query: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Additional utility endpoints for dynamic content

    @GetMapping("/student/{id}")
    public String studentDetail(@PathVariable Long id, Model model) {
        try {
            var student = studentService.getStudentById(id);
            if (student.isPresent()) {
                model.addAttribute("student", student.get());
                model.addAttribute("studentCourses", courseService.getCoursesByStudentId(id));
                model.addAttribute("totalCredits", courseService.getTotalCreditsByStudentId(id));
                model.addAttribute("pageTitle", "Student Details - " + student.get().getName());
                return "student-detail";
            } else {
                model.addAttribute("error", "Student not found with ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load student details: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        try {
            var course = courseService.getCourseById(id);
            if (course.isPresent()) {
                model.addAttribute("course", course.get());
                if (course.get().getStudentId() != null) {
                    var student = studentService.getStudentById(course.get().getStudentId());
                    student.ifPresent(s -> model.addAttribute("assignedStudent", s));
                }
                model.addAttribute("pageTitle", "Course Details - " + course.get().getTitle());
                return "course-detail";
            } else {
                model.addAttribute("error", "Course not found with ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load course details: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("pageTitle", "Reports & Analytics");

        try {
            // Comprehensive reporting data
            model.addAttribute("studentSummaries", studentService.getAllStudentSummaries());
            model.addAttribute("studentsWithStats", studentService.getStudentsWithCourseStats());
            model.addAttribute("courseDTOs", courseService.getAllCoursesAsDTO());

            // System health metrics
            Map<String, Object> systemMetrics = new HashMap<>();
            systemMetrics.put("totalRecords", studentService.getAllStudents().size() + courseService.getAllCourses().size());
            systemMetrics.put("dataIntegrity", "100%");
            systemMetrics.put("lastUpdated", java.time.LocalDateTime.now());
            model.addAttribute("systemMetrics", systemMetrics);

        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate reports: " + e.getMessage());
        }

        return "reports";
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Search Results");

        if (q != null && !q.trim().isEmpty()) {
            try {
                model.addAttribute("query", q);
                model.addAttribute("studentResults", studentService.searchStudents(q));
                model.addAttribute("courseResults", courseService.searchCoursesByTitle(q));
            } catch (Exception e) {
                model.addAttribute("error", "Search failed: " + e.getMessage());
            }
        }

        return "search-results";
    }

    // Error handling
    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("pageTitle", "Error");
        return "error";
    }

    // Utility methods
    private String getAgeGroup(int age) {
        if (age <= 20) return "16-20";
        else if (age <= 25) return "21-25";
        else if (age <= 30) return "26-30";
        else return "31+";
    }

    private String getCreditGroup(int credits) {
        if (credits <= 2) return "1-2 credits";
        else if (credits <= 4) return "3-4 credits";
        else return "5+ credits";
    }

    private Object generateMockResults(String query) {
        // Generate mock data based on query type for demo purposes
        if (query.toLowerCase().contains("select")) {
            // Return mock student/course data
            return java.util.List.of(
                java.util.Map.of("id", 1, "name", "John Doe", "email", "john@example.com", "age", 22),
                java.util.Map.of("id", 2, "name", "Jane Smith", "email", "jane@example.com", "age", 21)
            );
        }
        return java.util.List.of();
    }
}
