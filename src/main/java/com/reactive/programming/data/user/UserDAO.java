package com.reactive.programming.data.user;

import com.reactive.programming.data.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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

        String sql = " INSERT INTO users (username, email, first_name, last_name, active, created_at, updated_at) " +
                " VALUES (:username, :email, :firstName, :lastName, :active, :createdAt, :updatedAt) " +
                " RETURNING user_id, username, email, first_name, last_name, active, created_at, updated_at";

        return databaseClient.sql(sql)
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

    public Mono<UserEntity> getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);

        return databaseClient.sql(
                        "SELECT user_id, username, email, first_name, last_name, active, created_at, updated_at " +
                                "FROM users WHERE user_id = :userId"
                )
                .bind("userId", userId)
                .map((row, metadata) -> {
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
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.info("User found with ID: {}", u.getUserId());
                    } else {
                        log.warn("User not found with id: {} returned null", userId);
                    }
                })
                .switchIfEmpty(Mono.empty());
    }
    
    
    public Flux<UserEntity> getAllUsers() {
        log.info("Fetching all users");

        return databaseClient.sql(
                        "SELECT user_id, username, email, first_name, last_name, active, created_at, updated_at " +
                                "FROM users ORDER BY user_id"
                )
                .map((row, metadata) -> {
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
                .all()
                .doOnComplete(() -> log.info("All users fetched"));
    }
    
    public Mono<UserEntity> updateUser(Long userId, UserEntity userEntity) {
        log.info("Updating user with ID: {}", userId);

        return getUserById(userId)
                .flatMap(existingUser -> databaseClient.sql(
                                "UPDATE users SET username = :username, email = :email, " +
                                        "first_name = :firstName, last_name = :lastName, active = :active, " +
                                        "updated_at = :updatedAt WHERE user_id = :userId " +
                                        "RETURNING user_id, username, email, first_name, last_name, active, created_at, updated_at"
                        )
                        .bind("username", userEntity.getUserName())
                        .bind("email", userEntity.getEmail())
                        .bind("firstName", userEntity.getFirstName() != null ? userEntity.getFirstName() : "")
                        .bind("lastName", userEntity.getLastName() != null ? userEntity.getLastName() : "")
                        .bind("active", userEntity.getActive() != null ? userEntity.getActive() : true)
                        .bind("updatedAt", LocalDateTime.now())
                        .bind("userId", userId)
                        .map((row, metadata) -> {
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
                        .doOnSuccess(dto -> log.info("User updated: {}", dto.getUserName())));
    }

    public Mono<Long> deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        return databaseClient.sql("DELETE FROM users WHERE user_id = :userId")
                .bind("userId", userId)
                .fetch()
                .rowsUpdated()
                .map(Long::valueOf)
                .doOnSuccess(rows -> {
                    if (rows > 0) {
                        log.info("User deleted successfully with ID: {}", userId);
                    } else {
                        log.warn("No user found with ID: {}", userId);
                    }
                });
    }

}
