package com.david.citas.service;

import com.david.citas.dto.CitaRequest;
import com.david.citas.dto.CitaResponse;
import com.david.citas.entity.Cita;
import com.david.citas.enums.EstadoCita;
import com.david.citas.mapper.CitaMapper;
import com.david.citas.repository.CitaRepository;
import com.david.commons.enums.EstadoRegistro;
import com.david.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class CitaServiceImpl implements CitaService{
    private final CitaMapper citaMapper;
    private final CitaRepository citaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> lista() {
        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(citaMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        return citaMapper.entidadAResponse(obtenerCita(id));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando nueva cita...");

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        return citaMapper.entidadAResponse(cita);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCita(id);

        log.info("Actualizando cita con id: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");

        return citaMapper.entidadAResponse(cita);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCita(id);

        log.info("Eliminandocita con id: {}...", id);

        cita.eliminar();

        log.info("Cita eliminada.");

    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstado) {
        Cita cita = obtenerCita(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstado));

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);

    }

    private Cita obtenerCita(Long id){
        log.info("Buscando cita con id: {}",id);

        return citaRepository.findById(id).
                orElseThrow(
                        ()-> new RecursoNoEncontradoException("Cita no encontrada con id: "+ id)
                );
    }
}
