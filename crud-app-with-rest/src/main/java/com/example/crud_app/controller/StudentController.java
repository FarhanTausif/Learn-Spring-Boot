package com.example.crud_app.controller;


import com.example.crud_app.model.Student;
import com.example.crud_app.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Get all students
    @GetMapping
    public List<Student> list() {
        return studentService.getAllStudents();
    }
    
    // Get student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
    
    // Create a new student
    @PostMapping
    public String createStudent(@RequestBody Student student) {
        studentService.saveStudent(student);
        return "New Student with id " + student.getStudentId() + " has been created";
    }
    
    // Update Student
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student student = studentService.getStudentById(id);

        // This might be done with DTO (Due for tommorow)
        if (student != null) {
            if(updatedStudent.getName() != null) student.setName(updatedStudent.getName());
            if(updatedStudent.getEmail() != null) student.setEmail(updatedStudent.getEmail());
            if(updatedStudent.getAge() > 0) student.setAge(updatedStudent.getAge());
            studentService.saveStudent(student);
        } else {
            throw new RuntimeException("Student with id " + id + " not found");
        }
        return student;
    }

    // Delete student by ID
    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return "Student with id " + id + " has been deleted";
    }
}
