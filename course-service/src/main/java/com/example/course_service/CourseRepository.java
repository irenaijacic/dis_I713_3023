package com.example.course_service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.course_service.models.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findById(long id);

    Course getByName(String name);

    List<Course> findByCategoryIgnoreCase(String category);

    @Query("SELECT c FROM Course c WHERE " +
    "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
    "(:category IS NULL OR LOWER(c.category) = LOWER(:category))")
    List<Course> searchCourses(@Param("name") String name, @Param("category") String category);

    @Query("SELECT c.name FROM Course c JOIN c.enrolledUserIds e WHERE e = :userId")
    List<String> findCourseNamesByUserId(@Param("userId") Long userId);

}
