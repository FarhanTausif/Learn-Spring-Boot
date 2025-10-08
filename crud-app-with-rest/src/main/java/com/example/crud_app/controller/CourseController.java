package com.example.crud_app.controller;

import com.example.crud_app.model.Course;
import com.example.crud_app.repository.CourseRepository;
import com.example.crud_app.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Get all courses
    @GetMapping
    public List<Course> findAllCourses(){
        return courseService.getAllCourses();
    }

    // Get Courses By ID
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id){
        return courseService.getCourseById(id);
    }

    // Create a new course
    @PostMapping
    public Course createCourse(@RequestBody Course course){
        courseService.saveCourse(course);
        return course;
    }

    // Update existing course
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse){
        Course course = courseService.getCourseById(id);
        if(course != null){
            course.setTitle(updatedCourse.getTitle());
            course.setCredits(updatedCourse.getCredits());
            course.setStudent(updatedCourse.getStudent());
            courseService.saveCourse(course);
        }else {
            throw new RuntimeException("Course with id " + id + " not found");
        }
        return course;
    }

    //Delete course by ID
    @DeleteMapping("/{id}")
    public String deleteCourseById(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return "Course with id " + id + " has been deleted";
    }
}
