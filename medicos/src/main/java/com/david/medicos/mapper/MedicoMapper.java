package com.david.medicos.mapper;

import com.david.commons.dto.medicos.MedicoRequest;
import com.david.commons.dto.medicos.MedicoResponse;
import com.david.commons.mapper.CommonMapper;
import com.david.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, Medico, MedicoResponse> {
    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        return request == null
                ? null
                : Medico.crear(
                        request.nombre(),
                        request.apellidoPaterno(),
                        request.apellidoMaterno(),
                        request.edad(),
                        request.email(),
                        request.telefono(),
                        request.cedulaProfesional());
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        return entidad == null
                ? null
                : new MedicoResponse(
                    entidad.getId(),
                    entidad.obtenerNombreCompleto(),
                    entidad.getEdad(),
                    entidad.getEmail(),
                    entidad.getTelefono(),
                    entidad.getCedulaProfesional(),
                    entidad.getEspecialidad().toString(),
                    entidad.getDisponibilidad().toString(),
                    entidad.getDisponibilidad().getCodigo());
    }
}
