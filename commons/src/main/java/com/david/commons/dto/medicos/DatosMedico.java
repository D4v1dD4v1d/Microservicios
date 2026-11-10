package com.david.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un médico asociado a una cita")
public record DatosMedico(
        @Schema(description = "Nombre completo del médico", example = "David Pérez Zavala")
        String nombre,
        @Schema(description = "Cédula profesional del médico", example = "123022456987")
        String cedulaProfesional,
        @Schema(description = "Nombre de la especialidad del médico", example = "Cardiología")
        String especialidad
) {}
