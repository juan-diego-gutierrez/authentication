package co.com.pragma.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.exception.RequestValidator;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserHandlerTest {

  private UserUseCase userUseCase;
  private UserMapper userMapper;
  private UserHandler userHandler;
  private RequestValidator requestValidator;

  @BeforeEach
  void setUp() {
    userUseCase = mock(UserUseCase.class);
    userMapper = mock(UserMapper.class);
    requestValidator = mock(RequestValidator.class);
    userHandler = new UserHandler(userUseCase, userMapper, requestValidator);
  }

  @Test
  void testRegisterUser_Success() {
    UserDTO userDTO = new UserDTO("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St",
        "1234567890", "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");

    when(requestValidator.validate(any())).thenReturn(Mono.just(userDTO));
    when(userMapper.toUser(userDTO)).thenReturn(user);
    when(userMapper.toUserDTO(user)).thenReturn(userDTO);
    when(userUseCase.saveUser(user)).thenReturn(Mono.just(user));

    ServerRequest serverRequest = mock(ServerRequest.class);
    when(serverRequest.bodyToMono(UserDTO.class)).thenReturn(Mono.just(userDTO));
    when(serverRequest.path()).thenReturn("/api/v1/users");

    StepVerifier.create(userHandler.registerUser(serverRequest))
        .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
        .verifyComplete();
  }

  @Test
  void testGetUserByEmail_Success() {
    String email = "john.doe@example.com";
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        email, BigDecimal.valueOf(5000), 1L, "12345");

    when(userUseCase.getUserByEmail(email)).thenReturn(Mono.just(user));

    ServerRequest serverRequest = mock(ServerRequest.class);
    when(serverRequest.pathVariable("email")).thenReturn(email);

    StepVerifier.create(userHandler.getUserByEmail(serverRequest))
        .expectNextMatches(response -> response.statusCode()
            == HttpStatus.OK)
        .verifyComplete();
  }

  @Test
  void testGetUserByEmail_NotFound() {
    String email = "nonexistent@example.com";

    when(userUseCase.getUserByEmail(email)).thenReturn(Mono.empty());

    ServerRequest serverRequest = mock(ServerRequest.class);
    when(serverRequest.pathVariable("email")).thenReturn(email);

    StepVerifier.create(userHandler.getUserByEmail(serverRequest))
        .expectNextMatches(response -> response.statusCode()
            == HttpStatus.NOT_FOUND)
        .verifyComplete();
  }
}
