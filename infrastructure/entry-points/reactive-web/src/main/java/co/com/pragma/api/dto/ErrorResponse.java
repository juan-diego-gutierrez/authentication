package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    @Schema(example = "error") String status,
    Map<String, String> errors,
    @Schema(example = "Validation failed") String message,
    @Schema(example = "2025-08-24T16:45:00") LocalDateTime timestamp,
    @Schema(example = "/api/users") String path) {

}

