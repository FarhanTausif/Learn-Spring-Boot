package com.example.crud_app.repository;

import com.example.crud_app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Derived query methods - Spring generates JPQL automatically!

    // Find students by age greater than specified value
    List<Student> findByAgeGreaterThan(Integer age);

    // Find students by age between range
    List<Student> findByAgeBetween(Integer minAge, Integer maxAge);

    // Find students by email containing pattern (case-insensitive)
    List<Student> findByEmailContainingIgnoreCase(String emailPattern);

    // Find student by exact email
    Optional<Student> findByEmail(String email);

    // Find students by name containing pattern (case-insensitive)
    List<Student> findByNameContainingIgnoreCase(String namePattern);

    // Find students by name or email containing pattern
    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String namePattern, String emailPattern);

    // Find students older than age, ordered by age descending
    List<Student> findByAgeGreaterThanOrderByAgeDesc(Integer age);

    // Find students by age, ordered by name
    List<Student> findByAgeOrderByNameAsc(Integer age);

    // Count students by age
    Long countByAge(Integer age);

    // Count students older than specified age
    Long countByAgeGreaterThan(Integer age);

    // Check if student exists by email
    boolean existsByEmail(String email);

    // Find students by name starting with prefix
    List<Student> findByNameStartingWithIgnoreCase(String namePrefix);

    // Find top N students ordered by age descending
    List<Student> findTop5ByOrderByAgeDesc();

    // Find students by age less than
    List<Student> findByAgeLessThan(Integer age);

    // Find students by name and age
    Optional<Student> findByNameAndAge(String name, Integer age);
}
