package com.reactive.programming.ui.controller.user;

import com.reactive.programming.service.user.UserService;
import com.reactive.programming.shared.user.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("create")
    public Mono<ResponseEntity<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping("/get/{userId}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/list")
    public Flux<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update/{userId}")
    public Mono<ResponseEntity<UserDTO>> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{userId}")
    public Mono<ResponseEntity<Map<String, String>>> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.ok(
                        Map.of("message", "User deleted successfully",
                                "userId", userId.toString())
                )))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()))));
    }

}
