package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserPostgresRepository extends ReactiveCrudRepository<UserEntity, String>,
    ReactiveQueryByExampleExecutor<UserEntity> {

  Mono<UserEntity> findByEmail(String email);
}
