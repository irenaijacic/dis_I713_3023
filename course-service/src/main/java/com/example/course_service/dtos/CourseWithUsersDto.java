package com.example.course_service.dtos;

import java.util.List;

import com.example.course_service.dtos.UserDto;

public class CourseWithUsersDto {

    private Long id;
    private String name;
    private List<UserDto> enrolledUsers;
    private String description;
    private float price;
    private String category;
    private int duration;
   

    public CourseWithUsersDto(Long id, String name, String description, float price, List<UserDto> enrolledUsers,
            String category, int duration) {
        this.id = id;
        this.name = name;
        this.enrolledUsers = enrolledUsers;
        this.description = description;
        this.price = price;
        this.category = category;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public List<UserDto> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(List<UserDto> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
