package com.example.crud_app.controller;


import com.example.crud_app.model.Student;
import com.example.crud_app.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Hello world from Thymeleaf + Spring Boot");
        return "index";
    }

    @GetMapping("/students")

    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/students/new")
    public String createNewStudent(Model model) {
        model.addAttribute("student", new Student());
        return "create_student";
    }

    @PostMapping("/students")
    public String saveStudent(@ModelAttribute Student student) {
        // For debugging
        System.out.println("Saving student: " + student.getName() + " | " + student.getEmail() + " | " + student.getAge());
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student_details";
    }

    @GetMapping("students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return "redirect:/students";
    }
}
