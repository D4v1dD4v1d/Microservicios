package com.david.citas.entity;


import com.david.citas.enums.EstadoCita;
import com.david.commons.enums.EstadoRegistro;
import com.david.commons.exceptions.ConflictoException;
import com.david.commons.exceptions.DatoInvalidoException;
import com.david.commons.utils.StringCustomUtils;
import com.david.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CITA")
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false, length = 500)
    private String sintomas;

    @Column(name = "ESTADO_CITA",nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    @Column(name = "ESTADO_RESGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;

    private static void  validarId(Long id, String campo){
        ValoresNumericosUtils.validadLongPositivo(id,
                "El id del "+ campo +" debe ser un entero positivo.");
    }

    private static void  validarFecha(LocalDateTime fecha){
        if (fecha == null || !fecha.isAfter(LocalDateTime.now()))
            throw new DatoInvalidoException("La fecha de la cita es requerida y debe ser futura");
    }

    private static void validarDatos(Long idPaciente, Long idMedico, String sintomas, LocalDateTime fechaCita){
        validarId(idPaciente, "paciente");

        validarId(idMedico, "medico");

        StringCustomUtils.validarTamanio(sintomas,20,500,
                "Los síntomas se deben describir entre 1 y 500 caracteres.");

        validarFecha(fechaCita);
    }

    private void validarNoEliminado(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("La cita ya está eliminada");
    }

    private void validarEliminacionPermitida(){

        validarNoEliminado();

        if (!estadoCita.isEliminable())
            throw new ConflictoException("La cita con estado "+estadoCita +" no puede eliminarse.");
    }

    private void validarActualizacionPermitida(){

        validarNoEliminado();

        if (!estadoCita.isActualizable())
            throw new ConflictoException("La cita con estado "+estadoCita +" no puede actualizarse.");
    }

    public void eliminar(){

        validarEliminacionPermitida();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;

    }

    public static Cita crear(Long idPaciente, Long idMedico, String sintomas, LocalDateTime fechaCita){

        validarDatos(idPaciente,idMedico,sintomas,fechaCita);

        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .sintomas(sintomas)
                .fechaCita(fechaCita)
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public void actualizar(Long idPaciente, Long idMedico, String sintomas, LocalDateTime fechaCita){

        validarActualizacionPermitida();

        validarDatos(idPaciente,idMedico,sintomas,fechaCita);

        this.idPaciente=idPaciente;
        this.idMedico=idMedico;
        this.sintomas=sintomas;
        this.fechaCita=fechaCita;
    }

    public void actualizarEstadoCita(EstadoCita nuevoEstado){

        validarActualizacionPermitida();

        if (nuevoEstado == null)
            throw new DatoInvalidoException("El nuevo estado de la cita es requerido");

        if (!estadoCita.puedeCambiarA(nuevoEstado))
            throw new ConflictoException("La cita con estado "+ estadoCita+" solo puede " +
                    "cambiar a: "+ estadoCita.puedeCambiar());

        this.estadoCita = nuevoEstado;
    }
}
