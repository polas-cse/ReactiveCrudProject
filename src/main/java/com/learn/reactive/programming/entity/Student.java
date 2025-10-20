package com.learn.reactive.programming.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    private String id;
    private String name;
    private String email;
    private String className;


    public Student(String name, String email, String className) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.className = className;
    }
}
