package com.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(defaultValue = "detailed information of the buyer")
public class CustomerDto {

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    @Schema(description = "name of the buyer", example = "Yurgen Prado Lopez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "emial of the buyer", example = "yurgen.prado@ias.com.co", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Document type is required")
    @Pattern(regexp = "^(CC|TI|CE)$", message = "Document type must be CC, TI, or CE")
    @Schema(description = "Type of document (CC,TI,CE)", example = "CC",allowableValues = {"CC","TI","CE"} ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String documentType;

    @NotBlank(message = "Document number is required")
    @Pattern(regexp = "^\\d{5,12}$", message = "Document number must be numeric and between 5 and 12 digits")
    @Schema(description = "Document number", example = "1091654234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String documentNumber;
}
