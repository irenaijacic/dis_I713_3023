package com.example.user_service.dtos;

public class UserDTO1 {

    Long id;
    String email;
    public long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UserDTO1(long id, String email) {
        this.id = id;
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
