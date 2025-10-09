package com.example.crud_app.service;

import com.example.crud_app.dto.CourseDTO;
import com.example.crud_app.model.Course;
import com.example.crud_app.model.Student;
import com.example.crud_app.repository.CourseRepository;
import com.example.crud_app.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    // Basic CRUD operations
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        course.setTitle(courseDetails.getTitle());
        course.setCredits(courseDetails.getCredits());
        course.setStudentId(courseDetails.getStudentId());

        return courseRepository.save(course);
    }

    public Course createCourseForStudent(Long studentId, Course course) {
        // Verify student exists
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        course.setStudentId(studentId);
        return courseRepository.save(course);
    }

    // DTO conversion methods
    public CourseDTO convertToCourseDTO(Course course) {
        String studentName = null;
        if (course.getStudentId() != null) {
            Optional<Student> student = studentRepository.findById(course.getStudentId());
            studentName = student.map(Student::getName).orElse("Unknown Student");
        }

        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .credits(course.getCredits())
                .studentId(course.getStudentId())
                .studentName(studentName)
                .build();
    }

    // DTO-based service methods
    public List<CourseDTO> getAllCourseDTOs() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToCourseDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByStudent(Long studentId) {
        return courseRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToCourseDTO)
                .collect(Collectors.toList());
    }

    // Methods using derived queries
    public List<Course> getCoursesByMinCredits(Integer minCredits) {
        return courseRepository.findByCreditsGreaterThanEqualOrderByCreditsDesc(minCredits);
    }

    public List<Course> getUnassignedCourses() {
        return courseRepository.findByStudentIdIsNull();
    }

    public List<Course> getAssignedCourses() {
        return courseRepository.findByStudentIdIsNotNull();
    }

    public List<Course> searchCoursesByTitle(String titlePattern) {
        return courseRepository.findByTitleContainingIgnoreCase(titlePattern);
    }

    public List<Course> getCoursesByCreditsRange(Integer minCredits, Integer maxCredits) {
        return courseRepository.findByCreditsBetween(minCredits, maxCredits);
    }

    public Integer getTotalCreditsByStudentId(Long studentId) {
        return courseRepository.findByStudentId(studentId)
                .stream()
                .mapToInt(Course::getCredits)
                .sum();
    }

    public Long countCoursesByStudent(Long studentId) {
        return courseRepository.countByStudentId(studentId);
    }

    public Long countUnassignedCourses() {
        return courseRepository.countByStudentIdIsNull();
    }

    public List<Course> getTopCoursesByCredits() {
        return courseRepository.findTop5ByOrderByCreditsDesc();
    }

    public boolean existsByTitle(String title) {
        return courseRepository.existsByTitle(title);
    }

    // Advanced DTO methods with aggregated data
    public List<CourseDTO> getCoursesWithStudentInfo() {
        return courseRepository.findByStudentIdIsNotNull()
                .stream()
                .map(this::convertToCourseDTO)
                .collect(Collectors.toList());
    }

    // Additional methods needed for the updated controller
    public List<CourseDTO> getAllCoursesAsDTO() {
        return getAllCourseDTOs();
    }

    public Optional<CourseDTO> getCourseDTOById(Long id) {
        return courseRepository.findById(id)
                .map(this::convertToCourseDTO);
    }

    public List<Course> getCoursesByStudentId(Long studentId) {
        return courseRepository.findByStudentId(studentId);
    }

    public List<Course> getCoursesByCredits(Integer credits) {
        return courseRepository.findByCredits(credits);
    }

    public Optional<Course> findByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public Long countCoursesByStudentId(Long studentId) {
        return courseRepository.countByStudentId(studentId);
    }

    public Long countCoursesByCredits(Integer credits) {
        return courseRepository.countByCredits(credits);
    }
}
