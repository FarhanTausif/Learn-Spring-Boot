package com.example.crud_app.repository;

import com.example.crud_app.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Derived query methods - Spring generates JPQL automatically!

    // Find courses by minimum credits (greater than or equal)
    List<Course> findByCreditsGreaterThanEqualOrderByCreditsDesc(Integer credits);

    // Find courses by exact credits
    List<Course> findByCredits(Integer credits);

    // Find courses by credits range
    List<Course> findByCreditsBetween(Integer minCredits, Integer maxCredits);

    // Find courses by title containing pattern (case-insensitive)
    List<Course> findByTitleContainingIgnoreCase(String titlePattern);

    // Find courses by exact title
    Optional<Course> findByTitle(String title);

    // Find courses by student ID (foreign key)
    List<Course> findByStudentId(Long studentId);

    // Find courses where student ID is null (unassigned courses)
    List<Course> findByStudentIdIsNull();

    // Find courses where student ID is not null (assigned courses)
    List<Course> findByStudentIdIsNotNull();

    // Find courses by title and credits
    Optional<Course> findByTitleAndCredits(String title, Integer credits);

    // Count courses by student ID
    Long countByStudentId(Long studentId);

    // Count courses by credits
    Long countByCredits(Integer credits);

    // Count unassigned courses
    Long countByStudentIdIsNull();

    // Check if course exists by title
    boolean existsByTitle(String title);

    // Find courses by title starting with prefix
    List<Course> findByTitleStartingWithIgnoreCase(String titlePrefix);

    // Find top courses by credits (highest first)
    List<Course> findTop5ByOrderByCreditsDesc();

    // Find courses by credits greater than, ordered by title
    List<Course> findByCreditsGreaterThanOrderByTitleAsc(Integer credits);

    // Delete courses by student ID
    void deleteByStudentId(Long studentId);
}
