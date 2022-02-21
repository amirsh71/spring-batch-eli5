package com.amirsh71.springbatcheli5.batch;

import com.amirsh71.springbatcheli5.domain.Student;
import com.amirsh71.springbatcheli5.domain.StudentRegistration;
import com.amirsh71.springbatcheli5.repository.StudentRegistrationRepository;
import com.amirsh71.springbatcheli5.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StudentRepository studentRepository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 50; i++) {
            Student student = new Student();
            student.setFirstName("firstname" + i);
            student.setLastName("lastname" + i);
            student.setAge(i);
            studentRepository.save(student);
        }
    }

    @Bean
    public Job registerStudentJob() {
        return jobBuilderFactory.get("registerStudentJob")
                .start(registerStudentStep())
                .build();
    }

    @Bean
    public Step registerStudentStep() {
        return stepBuilderFactory.get("registerStudentStep")
                .<Student, StudentRegistration>chunk(5)
                .reader(studentItemReader(null))
                .processor(processor())
                .writer(repositoryItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public HibernatePagingItemReader<Student> studentItemReader(EntityManagerFactory entityManagerFactory) {
        return new HibernatePagingItemReaderBuilder<Student>(){}
                .name("studentItemReader")
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from Student ")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<Student, StudentRegistration> processor() {
        return item -> {
            StudentRegistration studentRegistration = new StudentRegistration();
            studentRegistration.setStudent(item);
            studentRegistration.setLocalDateTime(LocalDateTime.now());
            return studentRegistration;
        };
    }

    @Bean
    public RepositoryItemWriter<StudentRegistration> repositoryItemWriter(
            StudentRegistrationRepository repository) {
        return new RepositoryItemWriterBuilder<StudentRegistration>()
                .repository(repository)
                .methodName("save")
                .build();
    }
}
