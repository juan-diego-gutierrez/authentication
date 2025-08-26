package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;


@Schema(
    name = "ErrorResponse",
    description = "Standard format for error responses",
    example = """
        {
          "status": "error",
          "errors": {
            "email": "must not be blank",
            "password": "size must be at least 8 characters"
          },
          "message": "Validation failed",
          "timestamp": "2025-08-24T16:45:00",
          "path": "/api/users"
        }
        """
)
public record ErrorResponse(
    @Schema(example = "error") String status,
    @Schema(description = "Key-value map of errors", example = """
          { "email": "must be a well-formed email address" }
        """)
    Map<String, String> errors,
    @Schema(example = "Validation failed") String message,
    @Schema(example = "2025-08-24T16:45:00") LocalDateTime timestamp,
    @Schema(example = "/api/users") String path) {

}

