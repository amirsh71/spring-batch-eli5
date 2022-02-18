package com.amirsh71.springbatcheli5.batch;

import com.amirsh71.springbatcheli5.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job calculateGradeJob() {
        return jobBuilderFactory.get("calculateGradeJob")
                .start(calculateGradeStep())
                .build();
    }

    @Bean
    public Step calculateGradeStep() {
        return stepBuilderFactory.get("calculateGradeStep")
                .<Student, Student>chunk(5)
                .reader(studentItemReader(null))
                .processor(scoresProcessor())
                .writer(itemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Student> studentItemReader(@Value("#{jobParameters['inputFile']}") String inputFilePath) {
        return new FlatFileItemReaderBuilder<Student>()
                .name("customerItemReader")
                .delimited()
                .names("firstName",
                        "lastName",
                        "age",
                        "scores")
                .fieldSetMapper(fieldSet -> {
                    Student student = new Student();
                    student.setFirstName(fieldSet.readRawString("firstName"));
                    student.setLastName(fieldSet.readRawString("firstName"));
                    student.setAge(fieldSet.readInt("age"));
                    List<Integer> scores = Arrays.stream(fieldSet.readRawString("scores").split(";"))
                            .map(Integer::valueOf).collect(Collectors.toList());
                    student.setScores(scores);
                    return student;
                })
                .resource(new ClassPathResource(inputFilePath))
                .build();
    }

    @Bean
    public ItemProcessor<Student, Student> scoresProcessor() {
        return item -> {
            item.setGrade(item.getScores().stream().mapToDouble(Integer::doubleValue).average().orElse(0));
            return item;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Student> itemWriter(@Value("#{jobParameters['outputFile']}") String outputFilePath) {
        return new FlatFileItemWriterBuilder<Student>()
                .name("itemWriter")
                .resource(new ClassPathResource(outputFilePath))
                .delimited()
                .delimiter(",")
                .names(new String[]{"firstName",
                        "lastName",
                        "grade"})
                .build();
    }
}
