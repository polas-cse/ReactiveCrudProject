package com.learn.reactive.programming.repo;

import com.learn.reactive.programming.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface StudentRepo  extends MongoRepository<Student, String> {}
