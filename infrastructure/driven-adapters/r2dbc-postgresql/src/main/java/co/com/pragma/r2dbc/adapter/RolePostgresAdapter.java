package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repository.RolePostgresRepository;
import co.com.pragma.usecase.user.exception.BusinessException;
import co.com.pragma.usecase.user.exception.ErrorCode;
import java.util.Map;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RolePostgresAdapter extends
    ReactiveAdapterOperations<Role, RoleEntity, String, RolePostgresRepository> implements
    RoleRepository {

  public RolePostgresAdapter(RolePostgresRepository repository, ObjectMapper mapper) {
    super(repository, mapper, d -> mapper.map(d, Role.class));
  }

  @Override
  public Mono<Role> getRoleById(Long id) {
    return repository.findRoleById(id).map(this::toEntity)
        .switchIfEmpty(
            Mono.error(new BusinessException(
                Map.of("applicationType", ErrorCode.ROLE_NOT_FOUND.getMessage()))));
  }

  @Override
  public Mono<Boolean> roleExists(Long id) {
    return repository.findRoleById(id)
        .map(applicationTypeEntity -> true)
        .defaultIfEmpty(false);
  }
}
