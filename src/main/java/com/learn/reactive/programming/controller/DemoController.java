package com.learn.reactive.programming.controller;

import com.learn.reactive.programming.entity.Customer;
import com.learn.reactive.programming.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @PostMapping("/customer/create")
    public Mono<Customer> createCustomer(@RequestBody Customer customer){
        return demoService.createCustomer(customer);
    }

    @PutMapping("/customer/update")
    public Mono<Customer> updateCustomer(@RequestBody Customer customer){
        return demoService.updateCustomer(customer);
    }

    @GetMapping("/customer/list")
    public Flux<Customer> getAllCustomer(){
        return demoService.getAllCustomer();
    }

    @GetMapping("/customer/details/{id}")
    public Mono<Customer> getCustomer(@PathVariable String id){
        return demoService.getCustomerDetails(id);
    }

    @DeleteMapping("/customer/delete/{id}")
    public Mono<Customer> deleteCustomer(@PathVariable String id){
        return demoService.getCustomerDetails(id);
    }

}
