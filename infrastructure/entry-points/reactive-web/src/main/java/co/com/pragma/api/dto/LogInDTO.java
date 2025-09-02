package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LogInDTO(
    @NotBlank(message = "Email cannot be empty")
    String email,
    @NotBlank(message = "Password cannot be empty")
    String password) {

}
