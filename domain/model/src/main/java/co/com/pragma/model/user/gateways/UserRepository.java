package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

  Mono<User> saveUser(User user);

  Flux<User> getAllUsers();

  Mono<User> getUserByEmail(String email);

  Mono<Boolean> userExists(String email);

  Mono<String> login(String email, String password);
}
