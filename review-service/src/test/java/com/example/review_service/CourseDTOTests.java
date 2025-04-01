package com.example.review_service;

import org.junit.jupiter.api.Test;

import com.example.review_service.dtos.CourseDTO;

import static org.junit.jupiter.api.Assertions.*;

class CourseDTOTests {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String name = "Java Programming";
        String description = "Learn Java basics";
        float price = 99.99f;

        // Act
        CourseDTO courseDTO = new CourseDTO(name, description, price);

        // Assert
        assertEquals(name, courseDTO.getName());
        assertEquals(description, courseDTO.getDescription());
        assertEquals(price, courseDTO.getPrice(), 0.001);
    }

    @Test
    void testSetters() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO("Initial Course", "Initial Description", 50.0f);
        
        String newName = "Python Course";
        String newDescription = "Learn Python";
        float newPrice = 149.99f;

        // Act
        courseDTO.setName(newName);
        courseDTO.setDescription(newDescription);
        courseDTO.setPrice(newPrice);

        // Assert
        assertEquals(newName, courseDTO.getName());
        assertEquals(newDescription, courseDTO.getDescription());
        assertEquals(newPrice, courseDTO.getPrice(), 0.001);
    }

    @Test
    void testWithEmptyStrings() {
        // Arrange & Act
        CourseDTO courseDTO = new CourseDTO("", "", 0.0f);

        // Assert
        assertEquals("", courseDTO.getName());
        assertEquals("", courseDTO.getDescription());
        assertEquals(0.0f, courseDTO.getPrice(), 0.001);
    }

    @Test
    void testWithNullStrings() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO("Test Course", "Test Description", 10.0f);

        // Act
        courseDTO.setName(null);
        courseDTO.setDescription(null);

        // Assert
        assertNull(courseDTO.getName());
        assertNull(courseDTO.getDescription());
    }

    @Test
    void testPriceWithDifferentValues() {
        // Test with zero price
        CourseDTO freeCourse = new CourseDTO("Free Course", "Free Description", 0.0f);
        assertEquals(0.0f, freeCourse.getPrice(), 0.001);

        // Test with positive price
        CourseDTO paidCourse = new CourseDTO("Paid Course", "Paid Description", 199.99f);
        assertEquals(199.99f, paidCourse.getPrice(), 0.001);

        // Test with negative price
        CourseDTO discountCourse = new CourseDTO("Discount Course", "Discount Description", -10.0f);
        assertEquals(-10.0f, discountCourse.getPrice(), 0.001);
    }

    @Test
    void testWithLongStrings() {
        // Arrange
        String longName = "A".repeat(100);
        String longDescription = "B".repeat(1000);
        float price = 99.99f;

        // Act
        CourseDTO courseDTO = new CourseDTO(longName, longDescription, price);

        // Assert
        assertEquals(longName, courseDTO.getName());
        assertEquals(longDescription, courseDTO.getDescription());
        assertEquals(price, courseDTO.getPrice(), 0.001);
    }

    @Test
    void testWithSpecialCharacters() {
        // Arrange
        String nameWithSpecialChars = "Course#1 @Special!";
        String descriptionWithSpecialChars = "Description with $pecial ch@racters & symbols!";
        float price = 299.99f;

        // Act
        CourseDTO courseDTO = new CourseDTO(nameWithSpecialChars, descriptionWithSpecialChars, price);

        // Assert
        assertEquals(nameWithSpecialChars, courseDTO.getName());
        assertEquals(descriptionWithSpecialChars, courseDTO.getDescription());
        assertEquals(price, courseDTO.getPrice(), 0.001);
    }

    @Test
    void testPriceWithDecimalPlaces() {
        // Arrange & Act
        CourseDTO course1 = new CourseDTO("Course 1", "Description 1", 99.99f);
        CourseDTO course2 = new CourseDTO("Course 2", "Description 2", 100.00f);
        CourseDTO course3 = new CourseDTO("Course 3", "Description 3", 99.999f);

        // Assert
        assertEquals(99.99f, course1.getPrice(), 0.001);
        assertEquals(100.00f, course2.getPrice(), 0.001);
        assertEquals(99.999f, course3.getPrice(), 0.001);
    }
}