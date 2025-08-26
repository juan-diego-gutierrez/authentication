package co.com.pragma.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UserDTO(
    @NotBlank(message = "Name cannot be empty")
    String name,
    @NotBlank(message = "Last name cannot be empty")
    String lastName,

    LocalDate birthDate,

    String address,

    String phone,
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must have a valid format")
    String email,
    @NotNull(message = "Base salary cannot be null")
    @DecimalMin(value = "0", message = "Base salary must be a value between 0 and 15,000,000")
    @DecimalMax(value = "15000000", message = "Base salary must be a value between 0 and 15,000,000")
    BigDecimal baseSalary
) {

}
