package com.example.payment_service.dtos;

public class CourseDTO {
    String name;
    String description;
    float price;

    public CourseDTO(String name, String description, float price) {
        this.description = description;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPrice(Float price) {
        this.price = price;
    }

}
