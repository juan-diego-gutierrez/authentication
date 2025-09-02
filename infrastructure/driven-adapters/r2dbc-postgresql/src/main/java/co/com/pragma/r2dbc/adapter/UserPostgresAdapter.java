package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.repository.RolePostgresRepository;
import co.com.pragma.r2dbc.repository.UserPostgresRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.security.jwt.JwtProvider;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserPostgresAdapter extends
    ReactiveAdapterOperations<User, UserEntity, String, UserPostgresRepository> implements
    UserRepository {

  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RolePostgresRepository rolePostgresRepository;

  public UserPostgresAdapter(UserPostgresRepository repository, ObjectMapper mapper,
      PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
      RolePostgresRepository rolePostgresRepository) {
    super(repository, mapper, d -> mapper.map(d, User.class));
    this.passwordEncoder = passwordEncoder;
    this.jwtProvider = jwtProvider;
    this.rolePostgresRepository = rolePostgresRepository;
  }

  @Override
  public Mono<User> saveUser(User user) {
    UserEntity userData = toData(user);
    userData.setPassword(passwordEncoder.encode(user.getPassword()));
    return repository.save(userData).map(this::toEntity);
  }

  @Override
  public Flux<User> getAllUsers() {
    return repository.findAll().map(this::toEntity);
  }

  @Override
  public Mono<User> getUserByEmail(String email) {
    return repository.findByEmail(email).map(this::toEntity);
  }

  @Override
  public Mono<Boolean> userExists(String email) {
    return repository.findByEmail(email)
        .map(userEntity -> true)
        .defaultIfEmpty(false);
  }

  @Override
  public Mono<String> login(String email, String password) {
    return repository.findByEmail(email)
        .flatMap(userEntity -> {
          if (passwordEncoder.matches(password, userEntity.getPassword())) {
            return rolePostgresRepository.findRoleById(userEntity.getRoleId())
                .flatMap(role -> Mono.just(jwtProvider.generateToken(userEntity, role.getName())));
          } else {
            return Mono.error(new Throwable("bad credentials"));
          }
        })
        .switchIfEmpty(Mono.error(new Throwable("User not found")));
  }
}
