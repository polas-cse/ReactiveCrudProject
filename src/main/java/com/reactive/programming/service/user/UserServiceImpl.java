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
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);

        return userDao.createUser(userEntity)
                .map(entity -> modelMapper.map(entity, UserDTO.class))
                .doOnSubscribe(sub -> log.info("Creating user: {}", userDTO.getUserName()))
                .doOnSuccess(dto -> log.info("User created: {}", dto))
                .doOnError(err -> log.error("Failed to create user: {}", err.getMessage()));
    }

    @Override
    public Mono<UserDTO> getUserById(Long id) {
        return null;
    }

    @Override
    public Flux<UserDTO> getAllUsers() {
        return null;
    }

    @Override
    public Mono<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return null;
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return null;
    }
}
