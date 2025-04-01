package com.example.user_service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.user_service.dtos.UserDto;

class UserDtoTests {

    @Test
    void testUserDtoConstructor() {
        // Arrange & Act
        String username = "testUser";
        String email = "test@test.com";
        UserDto userDto = new UserDto(username, email);

        // Assert
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void testUserDtoSettersAndGetters() {
        // Arrange
        UserDto userDto = new UserDto("oldUser", "old@test.com");
        
        // Act
        String newUsername = "newUser";
        String newEmail = "new@test.com";
        userDto.setUsername(newUsername);
        userDto.setEmail(newEmail);

        // Assert
        assertEquals(newUsername, userDto.getUsername());
        assertEquals(newEmail, userDto.getEmail());
    }

    @Test
    void testUserDtoWithEmptyStrings() {
        // Arrange & Act
        UserDto userDto = new UserDto("", "");

        // Assert
        assertEquals("", userDto.getUsername());
        assertEquals("", userDto.getEmail());
    }

    @Test
    void testSetUsername() {
        // Arrange
        UserDto userDto = new UserDto("oldUsername", "test@test.com");
        String newUsername = "newUsername";

        // Act
        userDto.setUsername(newUsername);

        // Assert
        assertEquals(newUsername, userDto.getUsername());
    }

    @Test
    void testSetEmail() {
        // Arrange
        UserDto userDto = new UserDto("testUser", "old@test.com");
        String newEmail = "new@test.com";

        // Act
        userDto.setEmail(newEmail);

        // Assert
        assertEquals(newEmail, userDto.getEmail());
    }
}