package com.david.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record PacienteResponse(
        @Schema(description = "Identificador único del paciente", example = "1")
        Long id,
        @Schema(description = "Nombre completo del paciente", example = "David Pérez Zavala")
        String nombre,
        @Schema(description = "Edad del paciente", example = "50")
        Short edad,
        @Schema(description = "Peso del paciente", example = "50")
        Double peso,
        @Schema(description = "Estatura del paciente", example = "1.75")
        Double estatura,
        @Schema(description = "Indice de masa corporal del paciente", example = "50")
        Double imc,
        @Schema(description = "Email del paciente", example = "david.perez@example.com")
        String email,
        @Schema(description = "Télefono del paciente", example = "5552346879")
        String telefono,
        @Schema(description = "Dirección del paciente", example = "Av. Reforma 123, Colonia Centro, Ciudad de México")
        String direccion,
        @Schema(description = "Número de expediente del paciente", example = "5X5X1X2X3X4X5X6X7X8X")
        String numeroExpediente
) {}
