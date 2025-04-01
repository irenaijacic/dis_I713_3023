package com.example.review_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.review_service.dtos.UserDTO;


class UserDTOTests {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String username = "testUser";
        String email = "test@example.com";

        // Act
        UserDTO userDTO = new UserDTO(username, email);

        // Assert
        assertEquals(username, userDTO.getUsername());
        assertEquals(email, userDTO.getEmail());
    }

    @Test
    void testSetters() {
        // Arrange
        UserDTO userDTO = new UserDTO("initialUser", "initial@example.com");
        String newUsername = "updatedUser";
        String newEmail = "updated@example.com";

        // Act
        userDTO.setUsername(newUsername);
        userDTO.setEmail(newEmail);

        // Assert
        assertEquals(newUsername, userDTO.getUsername());
        assertEquals(newEmail, userDTO.getEmail());
    }

    @Test
    void testWithEmptyStrings() {
        // Arrange & Act
        UserDTO userDTO = new UserDTO("", "");

        // Assert
        assertEquals("", userDTO.getUsername());
        assertEquals("", userDTO.getEmail());
    }

    @Test
    void testWithNullValues() {
        // Arrange
        UserDTO userDTO = new UserDTO("testUser", "test@example.com");

        // Act
        userDTO.setUsername(null);
        userDTO.setEmail(null);

        // Assert
        assertNull(userDTO.getUsername());
        assertNull(userDTO.getEmail());
    }

    @Test
    void testWithSpecialCharacters() {
        // Arrange
        String usernameWithSpecialChars = "user#123@special!";
        String emailWithSpecialChars = "special.user+test@domain-name.com";

        // Act
        UserDTO userDTO = new UserDTO(usernameWithSpecialChars, emailWithSpecialChars);

        // Assert
        assertEquals(usernameWithSpecialChars, userDTO.getUsername());
        assertEquals(emailWithSpecialChars, userDTO.getEmail());
    }

    @Test
    void testWithLongStrings() {
        // Arrange
        String longUsername = "a".repeat(50);
        String longEmail = "a".repeat(30) + "@" + "b".repeat(30) + ".com";

        // Act
        UserDTO userDTO = new UserDTO(longUsername, longEmail);

        // Assert
        assertEquals(longUsername, userDTO.getUsername());
        assertEquals(longEmail, userDTO.getEmail());
    }

    @Test
    void testWithVariousEmailFormats() {
        // Arrange & Act & Assert
        // Simple email
        UserDTO user1 = new UserDTO("user1", "simple@example.com");
        assertEquals("simple@example.com", user1.getEmail());

        // Email with subdomains
        UserDTO user2 = new UserDTO("user2", "test@sub.domain.com");
        assertEquals("test@sub.domain.com", user2.getEmail());

        // Email with plus addressing
        UserDTO user3 = new UserDTO("user3", "user+tag@example.com");
        assertEquals("user+tag@example.com", user3.getEmail());

        // Email with numbers and hyphens
        UserDTO user4 = new UserDTO("user4", "user-123@example-domain.com");
        assertEquals("user-123@example-domain.com", user4.getEmail());
    }

    @Test
    void testWithVariousUsernameFormats() {
        // Arrange & Act & Assert
        // Simple username
        UserDTO user1 = new UserDTO("simple_user", "email1@example.com");
        assertEquals("simple_user", user1.getUsername());

        // Username with numbers
        UserDTO user2 = new UserDTO("user123", "email2@example.com");
        assertEquals("user123", user2.getUsername());

        // Username with special characters
        UserDTO user3 = new UserDTO("user.name-123", "email3@example.com");
        assertEquals("user.name-123", user3.getUsername());

        // Username with mixed case
        UserDTO user4 = new UserDTO("UserName", "email4@example.com");
        assertEquals("UserName", user4.getUsername());
    }

    @Test
    void testSettersIndependently() {
        // Arrange
        UserDTO userDTO = new UserDTO("initialUser", "initial@example.com");

        // Act & Assert - Update username only
        userDTO.setUsername("newUsername");
        assertEquals("newUsername", userDTO.getUsername());
        assertEquals("initial@example.com", userDTO.getEmail());

        // Act & Assert - Update email only
        userDTO.setEmail("new@example.com");
        assertEquals("newUsername", userDTO.getUsername());
        assertEquals("new@example.com", userDTO.getEmail());
    }
}