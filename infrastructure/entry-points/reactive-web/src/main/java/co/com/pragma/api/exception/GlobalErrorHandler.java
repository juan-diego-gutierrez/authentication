package co.com.pragma.api.exception;

import co.com.pragma.api.dto.ErrorResponse;
import co.com.pragma.usecase.user.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

  private final ObjectMapper objectMapper;

  public GlobalErrorHandler() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  @NonNull
  public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
    HttpStatus status;
    Map<String, String> errors = new LinkedHashMap<>();
    String message;

    if (ex instanceof InvalidRequestException invalidEx) {
      status = HttpStatus.BAD_REQUEST;
      errors.putAll(invalidEx.getErrors());
      message = "Validation failed";
    } else if (ex instanceof BusinessException businessEx) {
      status = HttpStatus.CONFLICT;
      errors.putAll(businessEx.getErrors());
      message = "Conflict";
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      errors.put("error", ex.getClass().getSimpleName());
      message = "Unexpected error";
      log.error("Unhandled exception", ex);
    }

    var body = new ErrorResponse(
        "error",
        errors,
        message,
        LocalDateTime.now(),
        exchange.getRequest().getPath().value()
    );

    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
      try {
        byte[] json = objectMapper.writeValueAsBytes(body);
        return exchange.getResponse().bufferFactory().wrap(json);
      } catch (Exception e) {
        byte[] fallback = "{\"status\":\"error\",\"message\":\"Error serializing response\"}"
            .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().bufferFactory().wrap(fallback);
      }
    }));
  }
}

