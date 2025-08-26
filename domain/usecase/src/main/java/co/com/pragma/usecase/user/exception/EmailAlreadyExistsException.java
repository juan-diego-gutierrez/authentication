package co.com.pragma.usecase.user.exception;

public class EmailAlreadyExistsException extends RuntimeException{
  public EmailAlreadyExistsException() {
    super("Email already exists");
  }
}

