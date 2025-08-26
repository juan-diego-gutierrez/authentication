package co.com.pragma.api.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

  private final Validator validator;

  public <T> Mono<T> validate(T object) {
    return Mono.fromCallable(() -> {
      Set<ConstraintViolation<T>> violations = validator.validate(object);
      if (!violations.isEmpty()) {
        Map<String, String> errors = violations.stream()
            .collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (a, b) -> a,
                LinkedHashMap::new
            ));
        throw new InvalidRequestException(errors);
      }
      return object;
    }).subscribeOn(Schedulers.boundedElastic());
  }
}

