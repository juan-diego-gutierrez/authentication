package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.exception.UserNotFoundException;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserPostgresAdapter extends
    ReactiveAdapterOperations<User, UserEntity, String, UserPostgresRepository> implements
    UserRepository {

  public UserPostgresAdapter(UserPostgresRepository repository, ObjectMapper mapper) {
    super(repository, mapper, d -> mapper.map(d, User.class));
  }

  @Override
  public Mono<User> saveUser(User user) {
    return repository.save(toData(user)).map(this::toEntity);
  }

  @Override
  public Flux<User> getAllUsers() {
    return repository.findAll().map(this::toEntity);
  }

  @Override
  public Mono<User> getUserByEmail(String email) {
    return repository.findByEmail(email).map(this::toEntity)
        .switchIfEmpty(Mono.error(new UserNotFoundException("User not found for email: " + email)));
  }

  @Override
  public Mono<Boolean> userExists(String email) {
    return repository.findByEmail(email)
        .map(userEntity -> true)
        .defaultIfEmpty(false);
  }
}
