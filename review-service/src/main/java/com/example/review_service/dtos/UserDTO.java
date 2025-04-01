package com.example.review_service.dtos;

public class UserDTO {
    String email;
    String username;

    public UserDTO(String username, String email) {
        this.email = email;
        this.username = username;

    }

    public UserDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
