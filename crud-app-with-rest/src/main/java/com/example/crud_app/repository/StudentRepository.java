package com.example.crud_app.repository;

import com.example.crud_app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Additional query methods can be defined here if needed

    // 1. Get all students older than a certain age

    @Query("SELECT s FROM Student s WHERE s.age > :age")
    List<Student> findOlderThan(@Param("age") int age);

    // 2. Count how many students have enrolled in at least one course

    @Query("SELECT COUNT(DISTINCT s) FROM Student s JOIN s.courses c")
    //"SELECT COUNT(DISTINCT s) FROM Student s JOIN s.courses c" -- Explanation in details
    // This JPQL query counts the number of unique students (DISTINCT s) who have at least one associated course.
    // How? By performing an inner join (JOIN) between the Student entity (s) and its associated courses (c).
    Long countStudentsWithCourses();

    // 3. Find students who enrolled in a course with a specific title

    @Query("SELECT DISTINCT s FROM Student s JOIN s.courses c WHERE c.title = :title")
    List<Student> findByCourseTitle(@Param("title") String title);

    // 4. Find average age of students enrolled per course title

    @Query("SELECT c.title, AVG(s.age) FROM Student s JOIN s.courses c GROUP BY c.title")
    List<Object[]> findAverageAgePerCourseTitle();


}
