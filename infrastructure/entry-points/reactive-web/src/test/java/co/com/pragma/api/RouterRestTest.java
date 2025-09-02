package co.com.pragma.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.pragma.api.dto.SuccessResponse;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.exception.RequestValidator;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class RouterRestTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private UserUseCase userUseCase;
  @MockitoBean
  private UserMapper userMapper;
  @MockitoBean
  private RequestValidator requestValidator;

  @Test
  void testRegisterUser_Success() {
    UserDTO userDTO = new UserDTO("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St",
        "1234567890", "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");

    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St",
        "1234567890", "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");

    when(requestValidator.validate(any())).thenReturn(Mono.just(userDTO));
    when(userMapper.toUser(userDTO)).thenReturn(user);
    when(userMapper.toUserDTO(user)).thenReturn(userDTO);
    when(userUseCase.saveUser(user)).thenReturn(Mono.just(user));

    webTestClient.post()
        .uri("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(userDTO)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(SuccessResponse.class)
        .value(
            response -> Assertions.assertThat(response.status()).isEqualTo("success"));
  }

  @Test
  void testGetAllUsers_Success() {
    User user1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        "john.doe@example.com", BigDecimal.valueOf(5000), 1L, "12345");
    User user2 = new User("Jane", "Doe", LocalDate.of(1992, 1, 1), "456 Main St", "0987654321",
        "jane.doe@example.com", BigDecimal.valueOf(6000), 1L, "12345");

    when(userUseCase.getAllUsers()).thenReturn(Flux.just(user1, user2));

    webTestClient.get()
        .uri("/api/v1/users")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(User.class)
        .hasSize(2)
        .consumeWith(response -> {
          List<User> users = response.getResponseBody();

          User actualUser1 = users.get(0);
          Assertions.assertThat(actualUser1.getEmail()).isEqualTo(user1.getEmail());

          User actualUser2 = users.get(1);
          Assertions.assertThat(actualUser2.getEmail()).isEqualTo(user2.getEmail());
        });
  }

  @Test
  void testGetUserByEmail_Success() {
    String email = "john.doe@example.com";
    User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", "1234567890",
        email, BigDecimal.valueOf(5000), 1L, "12345");

    when(userUseCase.getUserByEmail(email)).thenReturn(Mono.just(user));

    webTestClient.get()
        .uri("/api/v1/users/{email}", email)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(User.class)
        .value(currentUser -> Assertions.assertThat(currentUser.getEmail())
            .isEqualTo(user.getEmail()));
  }

  @Test
  void testGetUserByEmail_NotFound() {
    String email = "nonexistent@example.com";

    when(userUseCase.getUserByEmail(email)).thenReturn(Mono.empty());

    webTestClient.get()
        .uri("/api/v1/users/{email}", email)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }
}
