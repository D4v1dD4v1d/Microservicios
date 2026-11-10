package com.david.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un paciente asociado a una cita")
public record DatosPaciente(
        @Schema(description = "Nombre completo del paciente", example = "David Pérez Zavala")
        String nombre,
        @Schema(description = "Número expediente del paciente", example = "1X2X3X5X4X6X9X8X7X5X")
        String numExpediente,
        @Schema(description = "Edad del paciente", example = "35 años")
        String edad,
        @Schema(description = "Peso del paciente", example = "86 kg.")
        String peso,
        @Schema(description = "Estatura del paciente", example = "1.80 m.")
        String estatura,
        @Schema(description = "IMC del paciente", example = "22.2 Peso Normal")
        String imc,
        @Schema(description = "Télefono del paciente", example = "4451583269")
        String telefono
        ) {}
