package co.com.pragma.usecase.user;

import static org.mockito.ArgumentMatchers.any;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.exception.EmailAlreadyExistsException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserUseCaseTest {

  private UserRepository userRepository;
  private UserUseCase userUseCase;

  @BeforeEach
  void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    userUseCase = new UserUseCase(userRepository);
  }

  @Test
  void testSaveUser_Success() {
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000));

    Mockito.when(userRepository.userExists(user.getEmail())).thenReturn(Mono.just(false));
    Mockito.when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user));

    StepVerifier.create(userUseCase.saveUser(user))
        .expectNext(user)
        .verifyComplete();
  }

  @Test
  void testSaveUser_EmailAlreadyRegistered() {
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000));

    Mockito.when(userRepository.userExists(user.getEmail())).thenReturn(Mono.just(true));

    StepVerifier.create(userUseCase.saveUser(user))
        .expectError(EmailAlreadyExistsException.class)
        .verify();
  }

  @Test
  void testGetAllUsers() {
    User user1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000));
    User user2 = new User("Jane", "Doe", LocalDate.of(1992, 1, 1), "456 Main St", "0987654321",
        "jane.doe@example.com", BigDecimal.valueOf(6000));

    Mockito.when(userRepository.getAllUsers()).thenReturn(Flux.just(user1, user2));

    StepVerifier.create(userUseCase.getAllUsers())
        .expectNext(user1, user2)
        .verifyComplete();
  }

  @Test
  void testGetUserByEmail() {
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000));

    Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(Mono.just(user));

    StepVerifier.create(userUseCase.getUserByEmail(user.getEmail()))
        .expectNext(user)
        .verifyComplete();
  }
}
