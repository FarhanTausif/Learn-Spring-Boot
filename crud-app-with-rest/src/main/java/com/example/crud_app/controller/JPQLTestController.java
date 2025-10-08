package com.example.crud_app.controller;

import com.example.crud_app.model.Student;
import com.example.crud_app.repository.CourseRepository;
import com.example.crud_app.repository.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jpql")
public class JPQLTestController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public JPQLTestController(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/older/{age}")
    public List<Student> getOlderThan(@PathVariable int age){
        return studentRepository.findOlderThan(age);
    }

    @GetMapping("/with-courses/count")
    public Long countStudentsWithCourses() {
        return studentRepository.countStudentsWithCourses();
    }

    @GetMapping("/course/{title}")
    public List<Student> getCourseByTitle(@PathVariable String title){
        return studentRepository.findByCourseTitle(title);
    }

    @GetMapping("/average-age-per-course")
    public List<Map<String, Object>> getAverageAgePerCourse(){
        List<Object[]> results = studentRepository.findAverageAgePerCourseTitle();
        List<Map<String, Object>> list = new ArrayList<>();
        for(Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("courseTitle", row[0]);
            map.put("averageAge", row[1]);
            list.add(map);
        }
        return list;
    }

}
