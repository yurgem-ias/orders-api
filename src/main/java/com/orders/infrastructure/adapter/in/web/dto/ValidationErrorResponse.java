package com.orders.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "specific response structure for field validation errors")
public class ValidationErrorResponse extends ErrorResponse {
    private List<FieldErrorDto> validationErrors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Details of the error in a specific field of the dapyload")
    public static class FieldErrorDto {

        @Schema(description = "name or path of the field with validation problems")
        private String field;

        @Schema(description = "Explanation of the validation error for the field")
        private String message;
    }
}
