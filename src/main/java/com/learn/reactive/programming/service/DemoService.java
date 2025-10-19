package com.learn.reactive.programming.service;

import com.learn.reactive.programming.entity.Customer;
import com.learn.reactive.programming.repo.CustomerRepo;
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

    public Mono<Customer> createCustomer(Customer customer){
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID().toString());
        }
        Customer saveCustomer = customerRepo.save(customer);
        return Mono.just(saveCustomer);
    }

    public Mono<Customer> getCustomerDetails(String id){

        Optional<Customer> existingCustomer = customerRepo.findById(id);
        if(existingCustomer.isPresent()){
            return Mono.just(existingCustomer.get());
        }else {
            return Mono.empty();
        }

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




}
