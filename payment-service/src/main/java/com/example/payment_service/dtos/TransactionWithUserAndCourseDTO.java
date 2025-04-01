package com.example.payment_service.dtos;

import java.math.BigDecimal;

import com.example.payment_service.models.Transaction;

public class TransactionWithUserAndCourseDTO {
    private Transaction trans;
    private UserDTO user;
    private CourseDTO course;

    public TransactionWithUserAndCourseDTO(Transaction trans, UserDTO user,CourseDTO course) {
        this.trans = trans;
        this.user = user;
        this.course = course;
    }

    public TransactionWithUserAndCourseDTO() {
        //TODO Auto-generated constructor stub
    }

    public Transaction getTransaction() {
        return trans;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public void setTransaction(Transaction trans) {
        this.trans = trans;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setAmount(BigDecimal bigDecimal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAmount'");
    }

    public void setStatus(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStatus'");
    }
}
