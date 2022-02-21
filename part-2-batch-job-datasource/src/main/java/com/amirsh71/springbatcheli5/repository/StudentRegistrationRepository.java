package com.amirsh71.springbatcheli5.repository;

import com.amirsh71.springbatcheli5.domain.StudentRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRegistrationRepository extends CrudRepository<StudentRegistration, Long> {
}
