package com.reactive.programming.service.user;

import com.reactive.programming.data.user.UserDAO;
import com.reactive.programming.data.user.entity.UserEntity;
import com.reactive.programming.shared.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDao;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserDAO userDao, ModelMapper modelMapper) {
        this.userDao = userDao;
        this.modelMapper = modelMapper;
    }


    @Override
    public Mono<UserDTO> createUser(UserDTO userDTO) {

        return Mono.just(userDTO)
                .map(user-> modelMapper.map(userDTO, UserEntity.class))
                .flatMap(userDao::createUser)
                .map(user-> modelMapper.map(user, UserDTO.class))
                .doOnSubscribe(sub -> log.info("Creating user with username: {}", userDTO.getUserName()))
                .doOnSuccess(p -> log.info("User created: {}", p))
                .doOnError(err -> log.error("Error creating user: {}", err.getMessage()));
    }

    @Override
    public Mono<UserDTO> getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        return userDao.getUserById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .doOnSuccess(dto -> log.info("User found with user id: {}", userId))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + userId)));
    }

    @Override
    public Flux<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userDao.getAllUsers()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .doOnComplete(() -> log.info("All users fetched"));
    }

    @Override
    public Mono<UserDTO> updateUser(Long userId, UserDTO userDTO) {

        return Mono.just(userDTO)
                .map(user->modelMapper.map(userDTO, UserEntity.class))
                .flatMap(user-> userDao.updateUser(userId, user))
                .map(user-> modelMapper.map(user, UserDTO.class))
                .doOnSubscribe(sub -> log.info("update user with username: {}", userDTO.getUserName()))
                .doOnSuccess(p -> log.info("update user: {}", p))
                .doOnError(err -> log.error("Error update user: {}", err.getMessage()));
    }

    @Override
    public Mono<Void> deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        return userDao.deleteUser(userId)
                .flatMap(rows -> {
                    if (rows > 0) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new RuntimeException("User not found with ID: " + userId));
                    }
                });
    }
}
