package com.reactive.programming.data.user;

import com.reactive.programming.data.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class UserDAO {

    private final DatabaseClient databaseClient;

    @Autowired
    public UserDAO(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<UserEntity> createUser(UserEntity userEntity) {
        log.info("Creating user: {}", userEntity.getUserName());

        long userId = System.currentTimeMillis() % 100000000;

        String sql = " INSERT INTO users (user_id, username, email, first_name, last_name, active, created_at, updated_at) " +
                " VALUES (:userId, :username, :email, :firstName, :lastName, :active, :createdAt, :updatedAt) " +
                " RETURNING user_id, username, email, first_name, last_name, active, created_at, updated_at";

        return databaseClient.sql(sql)
                .bind("userId", userId)
                .bind("username", userEntity.getUserName())
                .bind("email", userEntity.getEmail())
                .bind("firstName", userEntity.getFirstName())
                .bind("lastName", userEntity.getLastName())
                .bind("active", userEntity.getActive() != null ? userEntity.getActive() : true)
                .bind("createdAt", LocalDateTime.now())
                .bind("updatedAt", LocalDateTime.now())
                .map((row, meta) -> {
                    UserEntity user = new UserEntity();
                    user.setUserId(row.get("user_id", Long.class));
                    user.setUserName(row.get("username", String.class));
                    user.setEmail(row.get("email", String.class));
                    user.setFirstName(row.get("first_name", String.class));
                    user.setLastName(row.get("last_name", String.class));
                    user.setActive(row.get("active", Boolean.class));
                    user.setCreatedAt(row.get("created_at", LocalDateTime.class));
                    user.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
                    return user;
                })
                .one()
                .doOnSuccess(u -> log.info("User created successfully with ID: {}", u.getUserId()))
                .doOnError(err -> log.error("Error creating user: {}", err.getMessage()));
    }

}
