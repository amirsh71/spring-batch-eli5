package com.amirsh71.springbatcheli5.repository;

import com.amirsh71.springbatcheli5.domain.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
}
