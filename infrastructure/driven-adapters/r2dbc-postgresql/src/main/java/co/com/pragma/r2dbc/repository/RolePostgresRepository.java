package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolePostgresRepository extends ReactiveCrudRepository<RoleEntity, String>,
    ReactiveQueryByExampleExecutor<RoleEntity> {

  Mono<RoleEntity> findRoleById(Long id);
}
