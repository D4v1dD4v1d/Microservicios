package com.david.citas.service;

import com.david.citas.dto.CitaRequest;
import com.david.citas.dto.CitaResponse;
import com.david.commons.service.CrudService;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {
    @Override
    List<CitaResponse> lista();

    @Override
    CitaResponse obtenerPorId(Long id);

    @Override
    CitaResponse registrar(CitaRequest request);

    @Override
    CitaResponse actualizar(CitaRequest request, Long id);

    @Override
    void eliminar(Long id);

    void actualizarEstadoCita(Long idCita, Long idEstado);

    void medicoTieneCitasAsignadas(Long idMedico);

    void pacienteTieneCitasAsignadas(Long idPaciente);
}
