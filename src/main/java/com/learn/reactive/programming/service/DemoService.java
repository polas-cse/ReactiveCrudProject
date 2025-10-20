package com.learn.reactive.programming.service;

import com.learn.reactive.programming.entity.Customer;
import com.learn.reactive.programming.entity.Student;
import com.learn.reactive.programming.repo.CustomerRepo;
import com.learn.reactive.programming.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DemoService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private StudentRepo studentRepo;

    public Mono<Customer> createCustomer(Customer customer){
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID().toString());
        }
        Customer saveCustomer = customerRepo.save(customer);
        return Mono.just(saveCustomer);
    }

    public Mono<Customer> getCustomerDetails(String id){

        Optional<Customer> existingCustomer = customerRepo.findById(id);
        return existingCustomer.map(Mono::just).orElseGet(Mono::empty);

    }

    public Flux<Customer> getAllCustomer(){
        List<Customer> allCustomer = customerRepo.findAll();
        return Flux.fromIterable(allCustomer);
    }

    public Mono<Customer> updateCustomer(Customer customer){
        Optional<Customer> existingCustomer = customerRepo.findById(customer.getId());

        if(existingCustomer.isPresent()) {
            Customer toUpdate = existingCustomer.get();
            toUpdate.setName(customer.getName());
            toUpdate.setJob(customer.getJob());
            Customer savedCustomer = customerRepo.save(toUpdate);
            return Mono.just(savedCustomer);
        } else {
            return Mono.empty();
        }

    }

    public Mono<Customer> deleteCustomer(String id) {

        Optional<Customer> existingCustomer = customerRepo.findById(id);
        if (existingCustomer.isPresent()) {
            customerRepo.delete(existingCustomer.get());
            return Mono.just(existingCustomer.get());
        } else {
            return Mono.error(new RuntimeException("Customer not found"));
        }

    }



    public Mono<Student> createStudent(Student student){
        if (student.getId() == null) {
            student.setId(UUID.randomUUID().toString());
        }
        Student saveCustomer = studentRepo.save(student);
        return Mono.just(saveCustomer);
    }

    public Mono<Student> getStudentDetails(String id){

        Optional<Student> existingCustomer = studentRepo.findById(id);
        return existingCustomer.map(Mono::just).orElseGet(Mono::empty);

    }

    public Flux<Student> getAllStudent(){
        List<Student> allCustomer = studentRepo.findAll();
        return Flux.fromIterable(allCustomer);
    }

    public Mono<Student> updateStudent(Student student){
        Optional<Student> existingCustomer = studentRepo.findById(student.getId());

        if(existingCustomer.isPresent()) {
            Student toUpdate = existingCustomer.get();
            toUpdate.setName(student.getName());
            toUpdate.setEmail(student.getEmail());
            toUpdate.setClassName(student.getClassName());
            Student savedCustomer = studentRepo.save(toUpdate);
            return Mono.just(savedCustomer);
        } else {
            return Mono.empty();
        }

    }

    public Mono<Student> deleteStudent(String id) {

        Optional<Student> existingCustomer = studentRepo.findById(id);
        if (existingCustomer.isPresent()) {
            studentRepo.delete(existingCustomer.get());
            return Mono.just(existingCustomer.get());
        } else {
            return Mono.error(new RuntimeException("Student not found"));
        }

    }

}
