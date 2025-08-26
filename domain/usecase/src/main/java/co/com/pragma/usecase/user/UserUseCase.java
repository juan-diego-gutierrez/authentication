package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.exception.EmailAlreadyExistsException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

  private final UserRepository userRepository;

  public Mono<User> saveUser(User user) {
    return userRepository.userExists(user.getEmail())
        .flatMap(exists -> {
          if (Boolean.TRUE.equals(exists)) {
            return Mono.error(new EmailAlreadyExistsException());
          }
          return userRepository.saveUser(user);
        });
  }

  public Flux<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public Mono<User> getUserByEmail(String email) {
    return userRepository.getUserByEmail(email);
  }
}
