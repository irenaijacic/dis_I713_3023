package com.example.user_service.dtos;

public class UserDto {

    String email;
    String username;

    public UserDto(String username, String email) {
        this.email = email;
        this.username = username;

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
