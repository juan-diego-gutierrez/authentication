package co.com.pragma.usecase.user;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.exception.BusinessException;
import co.com.pragma.usecase.user.exception.ErrorCode;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public Mono<User> saveUser(User user) {
    return roleRepository.roleExists(user.getRoleId())
        .flatMap(roleExists -> {
          if (Boolean.FALSE.equals(roleExists)) {
            return Mono.error(new BusinessException(
                Map.of("role", ErrorCode.ROLE_NOT_FOUND.getMessage())));
          }

          return userRepository.userExists(user.getEmail())
              .flatMap(userExists -> {
                if (Boolean.TRUE.equals(userExists)) {
                  return Mono.error(new BusinessException(
                      Map.of("email", ErrorCode.EMAIL_ALREADY_EXISTS.getMessage())));
                }
                return userRepository.saveUser(user);
              });
        });
  }

  public Flux<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public Mono<User> getUserByEmail(String email) {
    return userRepository.getUserByEmail(email);
  }

  public Mono<String> login(String email, String password) {
    return userRepository.login(email, password);
  }
}
