package com.david.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para crear o actualizar un paciente")
public record PacienteRequest(
        @Schema(description = "Nombre del paciente", example = "David")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Pérez")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "Zavala")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del paciente. Debe estar entre 18 y 100 años." ,example = "50")
        @NotNull(message = "La edad es requerida")
        @Max(value = 100, message = "La edad máxima es de 100 años")
        @Min(value = 1, message = "La edad minima es de 1 año")
        Short edad,

        @Schema(description = "Peso del paciente en kilogramos.", example = "60")
        @NotNull(message = "El peso es requerido")
        @DecimalMax(value = "200.0", message = "El peso máximo es de 200 kg")
        @DecimalMin(value = "0.1", message = "El peso mínimo es de 0.1 kg")
        Double peso,

        @Schema(description = "Estatura del paciente en metros", example = "1.53")
        @NotNull(message = "La estatura es requerida")
        @DecimalMax(value = "2.0", message = "La estatura máxima es de 2.0 m")
        @DecimalMin(value = "1.0", message = "La edad minima es de 1.0 m")
        Double estatura,

        @Schema(description = "Correo electrónico del paciente", example = "david.perez@example.com")
        @NotBlank(message = "El email es requerido")
        @Size (min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto (correo@dominio)")
        String email,

        @Schema(description = "Número telefónico de contacto. Debe contener exactamente 10 digitos.",
                example = "2222356486", pattern = "^[0-9]{10}$")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "[0-9](10)$", message = "El teléfono debe contener solo 10 digitos numéricos")
        String telefono,

        @Schema(description = "Dirección de vivienda del paciente",
                example = "Av. Reforma 123, Colonia Centro, Ciudad de México")
        @NotBlank(message = "La dirección es requerida")
        @Size (min = 1, max = 150, message = "La dirección debe tener entre 10 y 150 caracteres")
        String direccion

) {}
