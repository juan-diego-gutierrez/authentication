package co.com.pragma.api;

import co.com.pragma.api.dto.SuccessResponse;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.exception.RequestValidator;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

  private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
  private final UserUseCase userUseCase;
  private final UserMapper userMapper;
  private final RequestValidator requestValidator;

  @Operation(summary = "Register a new user", description = "This endpoint allows you to register a new user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User registered successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data")
  })
  @RequestBody(
      description = "User details for creation",
      required = true,
      content = @Content(schema = @Schema(implementation = UserDTO.class))
  )
  public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
    logger.info("Received request to register user");

    LocalDateTime timestamp = LocalDateTime.now();
    String path = serverRequest.path();

    return serverRequest.bodyToMono(UserDTO.class)
        .flatMap(requestValidator::validate)
        .flatMap(createUserDTO -> userUseCase.saveUser(userMapper.toUser(createUserDTO)))
        .map(userMapper::toUserDTO)
        .flatMap(userDTO -> ServerResponse.status(HttpStatus.CREATED)
            .bodyValue(new SuccessResponse<>(
                "success",
                Collections.singletonList(userDTO),
                "User created successfully",
                timestamp,
                path
            ))
        );
  }

  @Operation(summary = "Get User By Email", description = "This endpoint allows you to get an user by email.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns the user data"),
      @ApiResponse(responseCode = "400", description = "User not exists")
  })
  @Parameters(
      @Parameter(name = "email", description = "Email of the user to retrieve", required = true)
  )
  public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
    String email = serverRequest.pathVariable("email");
    return userUseCase.getUserByEmail(email)
        .flatMap(user -> ServerResponse.ok().bodyValue(user))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  @Operation(summary = "Get All Users", description = "This endpoint allows you to get all users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Returns all existing users"),
      @ApiResponse(responseCode = "400", description = "Users not exists")
  })
  public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
    return userUseCase.getAllUsers()
        .collectList()
        .flatMap(users -> ServerResponse.ok().bodyValue(users))
        .switchIfEmpty(ServerResponse.notFound().build());
  }
}
