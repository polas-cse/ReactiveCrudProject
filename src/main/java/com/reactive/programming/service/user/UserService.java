package com.reactive.programming.service.user;

import com.reactive.programming.shared.user.UserDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDTO> createUser(UserDTO userDTO);
    Mono<UserDTO> getUserById(Long userId);
    Flux<UserDTO> getAllUsers();
    Mono<UserDTO> updateUser(Long userId, UserDTO userDTO);
    Mono<Void> deleteUser(Long userId);

}
