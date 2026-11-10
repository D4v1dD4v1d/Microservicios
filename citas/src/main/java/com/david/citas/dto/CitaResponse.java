package com.david.citas.dto;

import com.david.commons.dto.medicos.DatosMedico;
import com.david.commons.dto.pacientes.DatosPaciente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Información de una cita")
public record CitaResponse(
        @Schema(description = "Identificador de la cita", example = "12")
        Long id,
        @Schema(description = "Información del paciente")
        DatosPaciente paciente,
        @Schema(description = "Información del médico")
        DatosMedico medico,
        @Schema(description = "Fecha y hora programada para la cita",
        example = "25/09/2026 10:50",
        type = "string",
        format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        @Schema(description = "Síntomas que presentó el paciente",
                example = "Dolor de cabeza severo por 3 días")
        String sintomas,
        @Schema(description = "Estado actual de la cita",
                example = "Confirmada por el paciente")
        String estadoCita
) {}
