package com.david.pacientes.mapper;

import com.david.commons.dto.pacientes.PacienteRequest;
import com.david.commons.dto.pacientes.PacienteResponse;
import com.david.commons.mapper.CommonMapper;
import com.david.pacientes.entitiy.Paciente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PacienteMapper implements CommonMapper<PacienteRequest, Paciente, PacienteResponse> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        return request == null
                ? null
                : Paciente.crear(
                        request.nombre(),
                        request.apellidoPaterno(),
                        request.apellidoMaterno(),
                        request.edad(),
                        request.peso(),
                        request.estatura(),
                        request.email(),
                        request.telefono(),
                        request.direccion());
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        return entidad == null
                ? null
                : new PacienteResponse(
                        entidad.getId(),
                        entidad.obtenerNombreCompleto(),
                        entidad.getEdad(),
                        entidad.getPeso(),
                        entidad.getEstatura(),
                        entidad.getImc(),
                        entidad.getEmail(),
                        entidad.getTelefono(),
                        entidad.getDireccion(),
                        entidad.getNumExpediente());
    }
}
