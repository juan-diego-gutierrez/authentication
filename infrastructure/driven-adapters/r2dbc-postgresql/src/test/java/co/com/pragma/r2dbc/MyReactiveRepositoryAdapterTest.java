package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.adapter.UserPostgresAdapter;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.repository.UserPostgresRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPostgresAdapterTest {

  @InjectMocks
  UserPostgresAdapter userPostgresAdapter;

  @Mock
  UserPostgresRepository userPostgresRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  ObjectMapper mapper;

  @Test
  void testSaveUser_Success() {
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");
    UserEntity userEntity = new UserEntity();
    userEntity.setName(user.getName());
    userEntity.setLastName(user.getLastName());
    userEntity.setBirthDate(user.getBirthDate());
    userEntity.setAddress(user.getAddress());
    userEntity.setPhone(user.getPhone());
    userEntity.setEmail(user.getEmail());
    userEntity.setBaseSalary(user.getBaseSalary());

    when(mapper.map(any(UserEntity.class), any())).thenReturn(user);
    when(mapper.map(any(User.class), any())).thenReturn(userEntity);
    when(userPostgresRepository.save(any())).thenReturn(Mono.just(userEntity));

    StepVerifier.create(userPostgresAdapter.saveUser(user))
        .expectNext(user)
        .verifyComplete();
  }

  @Test
  void testGetAllUsers_Success() {
    User user1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");
    User user2 = new User("Jane", "Doe", LocalDate.of(1992, 1, 1), "456 Main St", "0987654321",
        "jane.doe@example.com", BigDecimal.valueOf(6000), 1L, "12345");

    UserEntity userEntity1 = new UserEntity();
    userEntity1.setName(user1.getName());
    userEntity1.setLastName(user1.getLastName());
    userEntity1.setBirthDate(user1.getBirthDate());
    userEntity1.setAddress(user1.getAddress());
    userEntity1.setPhone(user1.getPhone());
    userEntity1.setEmail(user1.getEmail());
    userEntity1.setBaseSalary(user1.getBaseSalary());

    UserEntity userEntity2 = new UserEntity();
    userEntity2.setName(user2.getName());
    userEntity2.setLastName(user2.getLastName());
    userEntity2.setBirthDate(user2.getBirthDate());
    userEntity2.setAddress(user2.getAddress());
    userEntity2.setPhone(user2.getPhone());
    userEntity2.setEmail(user2.getEmail());
    userEntity2.setBaseSalary(user2.getBaseSalary());

    when(mapper.map(userEntity1, User.class)).thenReturn(user1);
    when(mapper.map(userEntity2, User.class)).thenReturn(user2);
    when(userPostgresRepository.findAll()).thenReturn(Flux.just(userEntity1, userEntity2));

    StepVerifier.create(userPostgresAdapter.getAllUsers())
        .expectNext(user1, user2)
        .verifyComplete();
  }

  @Test
  void testGetUserByEmail_Success() {
    String email = "john.doe@example.com";
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        email, BigDecimal.valueOf(5000), 1L, "12345");
    UserEntity userEntity = new UserEntity();
    userEntity.setName(user.getName());
    userEntity.setLastName(user.getLastName());
    userEntity.setBirthDate(user.getBirthDate());
    userEntity.setAddress(user.getAddress());
    userEntity.setPhone(user.getPhone());
    userEntity.setEmail(user.getEmail());
    userEntity.setBaseSalary(user.getBaseSalary());

    when(mapper.map(any(UserEntity.class), any())).thenReturn(user);
    when(userPostgresRepository.findByEmail(email)).thenReturn(Mono.just(userEntity));

    StepVerifier.create(userPostgresAdapter.getUserByEmail(email))
        .expectNext(user)
        .verifyComplete();
  }
}
