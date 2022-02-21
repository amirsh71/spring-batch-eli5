package com.amirsh71.springbatcheli5.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class StudentRegistration {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Student student;
    private LocalDateTime localDateTime;
}
