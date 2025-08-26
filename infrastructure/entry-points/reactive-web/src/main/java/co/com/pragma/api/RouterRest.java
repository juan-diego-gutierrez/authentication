package co.com.pragma.api;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

  private static final String USERS_PATH = "/api/v1/users";

  @Bean
  @RouterOperations({
      @RouterOperation(path = USERS_PATH, method = RequestMethod.POST, beanClass = UserHandler.class, beanMethod = "registerUser",
          operation = @Operation(summary = "Register a new user", description = "This endpoint allows you to register a new user.")),
      @RouterOperation(path = USERS_PATH, method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "getAllUsers",
          operation = @Operation(summary = "Get All Users", description = "This endpoint allows you to get all users.")),
      @RouterOperation(path = USERS_PATH
          + "/{email}", method = RequestMethod.GET, beanClass = UserHandler.class, beanMethod = "getUserByEmail",
          operation = @Operation(summary = "Get User By Email", description = "This endpoint allows you to get an user by email."))
  })
  public RouterFunction<ServerResponse> usersRoutes(UserHandler userHandler) {
    return route()
        .POST(USERS_PATH, userHandler::registerUser)
        .GET(USERS_PATH, userHandler::getAllUsers)
        .GET(USERS_PATH + "/{email}", userHandler::getUserByEmail)
        .build();
  }
}
