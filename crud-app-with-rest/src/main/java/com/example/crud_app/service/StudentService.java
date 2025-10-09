package com.example.crud_app.service;

import com.example.crud_app.dto.StudentSummaryDTO;
import com.example.crud_app.dto.StudentWithCoursesDTO;
import com.example.crud_app.model.Student;
import com.example.crud_app.model.Course;
import com.example.crud_app.repository.StudentRepository;
import com.example.crud_app.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // Basic CRUD operations
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        // First delete associated courses
        courseRepository.deleteByStudentId(id);
        // Then delete student
        studentRepository.deleteById(id);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setAge(studentDetails.getAge());

        return studentRepository.save(student);
    }

    // DTO conversion methods
    public StudentSummaryDTO convertToSummaryDTO(Student student) {
        return StudentSummaryDTO.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .email(student.getEmail())
                .build();
    }

    public StudentWithCoursesDTO convertToWithCoursesDTO(Student student) {
        List<Course> courses = courseRepository.findByStudentId(student.getStudentId());
        Integer totalCredits = courses.stream()
                .mapToInt(Course::getCredits)
                .sum();

        return StudentWithCoursesDTO.builder()
                .studentId(student.getStudentId())
                .name(student.getName())
                .email(student.getEmail())
                .age(student.getAge())
                .totalCourses(courses.size())
                .totalCredits(totalCredits)
                .build();
    }

    // DTO-based service methods
    public List<StudentSummaryDTO> getAllStudentSummaries() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<StudentWithCoursesDTO> getStudentsWithCourseStats() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToWithCoursesDTO)
                .collect(Collectors.toList());
    }

    // Methods using derived queries
    public List<Student> getStudentsOlderThan(Integer age) {
        return studentRepository.findByAgeGreaterThan(age);
    }

    public List<Student> searchStudents(String pattern) {
        return studentRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(pattern, pattern);
    }

    public List<Student> getStudentsByAgeRange(Integer minAge, Integer maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public List<StudentWithCoursesDTO> getStudentsWithoutCourses() {
        List<Student> allStudents = studentRepository.findAll();
        return allStudents.stream()
                .filter(student -> courseRepository.countByStudentId(student.getStudentId()) == 0)
                .map(this::convertToWithCoursesDTO)
                .collect(Collectors.toList());
    }

    public List<StudentWithCoursesDTO> getStudentsWithMinimumCourses(Integer minCourses) {
        List<Student> allStudents = studentRepository.findAll();
        return allStudents.stream()
                .filter(student -> courseRepository.countByStudentId(student.getStudentId()) >= minCourses)
                .map(this::convertToWithCoursesDTO)
                .collect(Collectors.toList());
    }

    public Long countStudentsWithCourses() {
        return studentRepository.findAll().stream()
                .filter(student -> courseRepository.countByStudentId(student.getStudentId()) > 0)
                .count();
    }

    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    public List<Student> getTopStudentsByAge() {
        return studentRepository.findTop5ByOrderByAgeDesc();
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }
}
