package com.example.course_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.course_service.dtos.CourseDto;

class CourseDtoTests {

    private CourseDto courseDto;
    private static final String TEST_NAME = "Java Programming";
    private static final String TEST_DESCRIPTION = "Learn Java Programming";
    private static final float TEST_PRICE = 100f;

    @BeforeEach
    void setUp() {
        courseDto = new CourseDto(TEST_NAME, TEST_DESCRIPTION, TEST_PRICE);
    }

    @Test
    void testConstructor() {
        assertEquals(TEST_NAME, courseDto.getName());
        assertEquals(TEST_DESCRIPTION, courseDto.getDescription());
        assertEquals(TEST_PRICE, courseDto.getPrice());
    }

    @Test
    void testSetAndGetName() {
        String newName = "Python Programming";
        courseDto.setName(newName);
        assertEquals(newName, courseDto.getName());
    }

    @Test
    void testSetAndGetDescription() {
        String newDescription = "Learn Python Programming";
        courseDto.setDescription(newDescription);
        assertEquals(newDescription, courseDto.getDescription());
    }

    @Test
    void testSetAndGetPrice() {
        float newPrice = 149.99f;
        courseDto.setPrice(newPrice);
        assertEquals(newPrice, courseDto.getPrice());
    }


}