package com.example.user_service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.user_service.dtos.CourseDto;

class CourseDtoTests {

    @Test
    void testCourseSettersAndGetters() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        String name = "Java Programming";
        String description = "Learn Java basics";
        Float price = 99.99f;

        // Act
        courseDto.setName(name);
        courseDto.setDescription(description);
        courseDto.setPrice(price);

        // Assert
        assertEquals(name, courseDto.getName());
        assertEquals(description, courseDto.getDescription());
        assertEquals(price, courseDto.getPrice());
    }

    @Test
    void testCourseWithEmptyStrings() {
        // Arrange
        CourseDto courseDto = new CourseDto();

        // Act
        courseDto.setName("");
        courseDto.setDescription("");
        courseDto.setPrice(0.0f);

        // Assert
        assertEquals("", courseDto.getName());
        assertEquals("", courseDto.getDescription());
        assertEquals(0.0f, courseDto.getPrice());
    }

    @Test
    void testCourseWithNullValues() {
        // Arrange
        CourseDto courseDto = new CourseDto();

        // Assert
        assertNull(courseDto.getName());
        assertNull(courseDto.getDescription());
        assertNull(courseDto.getPrice());
    }

    @Test
    void testSetName() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        String name = "Spring Boot Course";

        // Act
        courseDto.setName(name);

        // Assert
        assertEquals(name, courseDto.getName());
    }

    @Test
    void testSetDescription() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        String description = "Learn Spring Boot framework";

        // Act
        courseDto.setDescription(description);

        // Assert
        assertEquals(description, courseDto.getDescription());
    }

    @Test
    void testSetPrice() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        Float price = 149.99f;

        // Act
        courseDto.setPrice(price);

        // Assert
        assertEquals(price, courseDto.getPrice());
    }

    @Test
    void testPriceWithZeroValue() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        Float price = 0.0f;

        // Act
        courseDto.setPrice(price);

        // Assert
        assertEquals(price, courseDto.getPrice());
    }

    @Test
    void testPriceWithNegativeValue() {
        // Arrange
        CourseDto courseDto = new CourseDto();
        Float price = -99.99f;

        // Act
        courseDto.setPrice(price);

        // Assert
        assertEquals(price, courseDto.getPrice());
    }
}