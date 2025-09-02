package co.com.pragma.usecase.user.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  EMAIL_ALREADY_EXISTS("Email already exists"),
  USER_NOT_FOUND("User not found"),
  ROLE_NOT_FOUND("Role not found");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getCode() {
    return this.name();
  }

}


