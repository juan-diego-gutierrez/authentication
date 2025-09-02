package co.com.pragma.model.role.gateways;

import co.com.pragma.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {

  Mono<Role> getRoleById(Long id);
  Mono<Boolean> roleExists(Long id);
}
