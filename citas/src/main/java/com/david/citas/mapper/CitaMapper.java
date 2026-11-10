package com.david.citas.mapper;

import com.david.citas.dto.CitaRequest;
import com.david.citas.dto.CitaResponse;
import com.david.citas.entity.Cita;
import com.david.commons.dto.medicos.DatosMedico;
import com.david.commons.dto.medicos.MedicoResponse;
import com.david.commons.dto.pacientes.DatosPaciente;
import com.david.commons.dto.pacientes.PacienteResponse;
import com.david.commons.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, Cita, CitaResponse> {
    @Override
    public Cita requestAEntidad(CitaRequest request) {
        return request == null
                ? null
                : Cita.crear(
                        request.idPaciente(),
                        request.idMedico(),
                        request.sintomas(),
                        request.fechaCita());
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        return entidad == null
                ? null
                : new CitaResponse(
                        entidad.getId(),
                        null,
                        null,
                        entidad.getFechaCita(),
                        entidad.getSintomas(),
                        entidad.getEstadoCita().getDescripcion());
    }

    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        return entidad == null
                ? null
                : new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
    }

    private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente){
        return  paciente == null
                ? null
                : new DatosPaciente(
                        paciente.nombre(),
                        paciente.numeroExpediente(),
                        paciente.edad() + " años",
                        paciente.peso()+ " kg.",
                        paciente.estatura()+ " m.",
                        String.join(" ",
                                Math.round(paciente.imc() * 100.0)/ 100.0 +"",
                                        clasificacionIMC(paciente.imc())),
                        paciente.telefono());


    }

    private String clasificacionIMC(double imc){
        if(imc < 18.5) return "Bajo peso";
        if(imc < 25) return "Peso normal";
        if(imc < 30) return "Sobrepeso";
        if(imc < 35) return "Obesidad grado I";
        if(imc < 40) return "Obesidad grado II";
        return "Obesidad grado III";

    }

    private DatosMedico medicoResponseADatosMedico(MedicoResponse medico){
        return medico == null
                ? null
                : new DatosMedico(
                        medico.nombre(),
                        medico.cedulaProfesional(),
                        medico.especialidad());
    }
}
