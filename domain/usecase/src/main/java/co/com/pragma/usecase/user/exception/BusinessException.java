package co.com.pragma.usecase.user.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final Map<String, String> errors; //

  public BusinessException(Map<String, String> errors) {
    super("VALIDATION_FAILED");
    this.errors = errors;
  }
}


