package com.david.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

public record MedicoResponse(
        @Schema(description = "Identificador único del médico", example = "1")
        Long id,
        @Schema(description = "Nombre completo del médico", example = "David Pérez Zavala")
        String nombre,
        @Schema(description = "Edad del médico", example = "40")
        Short edad,
        @Schema(description = "Correo electrónico del médico", example = "david.p.z@example.com")
        String email,
        @Schema(description = "Télefono del médico", example = "1223245789")
        String telefono,
        @Schema(description = "Cédula profesional del médico", example = "5213365478")
        String cedulaProfesional,
        @Schema(description = "Especialidad del médico", example = "Cardiología")
        String especialidad,
        @Schema(description = "Descripción de la disponibilidad del médico", example = "Disponible para atender pacienetes")
        String disponibilidad,
        @Schema(description = "Identificados de la disponibilidad asociada al médico", example = "1")
        Long idDisponibilidad
) {}
