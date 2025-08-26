package co.com.pragma.api.exception;

import lombok.Getter;
import java.util.Map;

@Getter
public class InvalidRequestException extends RuntimeException {

  private final Map<String, String> errors;

  public InvalidRequestException(Map<String, String> errors) {
    super("Validation failed");
    this.errors = errors;
  }
}

