package com.amirsh71.springbatcheli5.domain;

import lombok.Data;

import java.util.List;

@Data
public class Student {
    private String firstName;
    private String lastName;
    private Integer age;
    private List<Integer> scores;
    private Double grade;
}
