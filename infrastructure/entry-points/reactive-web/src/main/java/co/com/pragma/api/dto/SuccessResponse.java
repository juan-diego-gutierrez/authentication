package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
    name = "SuccessResponse",
    description = "Standard format for successful responses",
    example = """
        {
          "status": "success",
          "data": [
            {
              "idUser": 1,
              "name": "Juan",
              "lastName": "Ramirez Hernandez",
              "address": "123 Main St, Lima",
              "phone": "987654321",
              "email": "ramirez@example.com",
              "baseSalary": 3500.50,
              "identificationDocument": "87654321",
              "dateOfBirth": "1995-05-20",
              "idRole": 2
            }
          ],
          "message": "Request processed successfully",
          "timestamp": "2025-08-24T16:45:00",
          "path": "/api/users"
        }
        """
)
public record SuccessResponse<T>(
    @Schema(description = "Status of the response, typically 'success'", example = "success")
    String status,

    @Schema(description = "List of data returned by the request", implementation = Object.class)
    List<T> data,

    @Schema(description = "Message describing the response", example = "Request processed successfully")
    String message,

    @Schema(description = "Timestamp when the response was generated", example = "2025-08-24T16:45:00")
    LocalDateTime timestamp,

    @Schema(description = "Request path that generated the response", example = "/api/users")
    String path) {

}

