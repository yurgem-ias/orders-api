package com.orders.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "standard response structure for general system errors")
public class ErrorResponse {

    @Schema(description = "date and time the error occurred")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code of the error response")
    private Integer status;

    @Schema(description = "descriptive code of the HTTP error category")
    private String error;

    @Schema(description = "readable message explaining the functional cause of the error")
    private String message;

    @Schema(description = "HTTP path or endpoint where the error occurred")
    private String path;
}
