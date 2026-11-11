package com.david.citas.service;

import com.david.citas.dto.CitaRequest;
import com.david.citas.dto.CitaResponse;
import com.david.citas.entity.Cita;
import com.david.citas.enums.EstadoCita;
import com.david.citas.mapper.CitaMapper;
import com.david.citas.repository.CitaRepository;
import com.david.commons.clients.MedicoClient;
import com.david.commons.clients.PacienteClient;
import com.david.commons.dto.medicos.MedicoResponse;
import com.david.commons.dto.pacientes.PacienteResponse;
import com.david.commons.enums.DisponibilidadMedico;
import com.david.commons.enums.EstadoRegistro;
import com.david.commons.exceptions.ConflictoException;
import com.david.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.david.citas.enums.EstadoCita.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class CitaServiceImpl implements CitaService{
    private final CitaMapper citaMapper;
    private final CitaRepository citaRepository;

    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private final List<EstadoCita> ESTADOS_ACTIVOS_INVALIDOS =
            List.of(CONFIRMADA, EN_CURSO);

    private final List<EstadoCita> ESTADO_PACIENTE_INVALIDO =
            List.of(CONFIRMADA,EN_CURSO, PENDIENTE);


    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> lista() {
        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        log.info("Obteniendo cita con id {}...", id);

        Cita cita = obtenerCita(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando nueva cita...");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        validarMedicoActivoDisponible(medico);

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        pacienteTieneCitasAsignadas(paciente.id());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCita(id);

        if (cita.getEstadoCita() == EN_CURSO)
            throw new ConflictoException("La cita esta en curso y ya no puede actualizarse");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        pacienteTieneCitasAsignadas(paciente.id());

        validarMedicoActivoDisponible(medico);

        log.info("Actualizando cita con id: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.sintomas(),
                request.fechaCita() );

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCita(id);

        log.info("Eliminando cita con id: {}...", id);

        cita.eliminar();

        citaRepository.saveAndFlush(cita);

        log.info("Cita eliminada.");

        if (cita.getEstadoCita() == PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(),DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstado) {
        Cita cita = obtenerCita(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstado));

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(),cita.getEstadoCita());

    }

    @Override
    @Transactional(readOnly = true)
    public void medicoTieneCitasAsignadas(Long idMedico) {
        log.info("Validando si el médico tiene una cita activa con los estados: {}");

        if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                ESTADOS_ACTIVOS_INVALIDOS))
            throw new ConflictoException("No se puede modificar o eliminar el médico ya que" +
                    "tiene citas en estado: "+ ESTADOS_ACTIVOS_INVALIDOS);

    }

    @Override
    @Transactional(readOnly = true)
    public void pacienteTieneCitasAsignadas(Long idPaciente) {
        log.info("Validando si el paciente tiene una cita activa con los estados: {}");

        if (citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                ESTADO_PACIENTE_INVALIDO))
            throw new ConflictoException("No se puede modificar o eliminar el paciente ya que" +
                    "tiene citas en estado: "+ ESTADO_PACIENTE_INVALIDO);
    }

    private Cita obtenerCita(Long id){
        log.info("Buscando cita con id: {}",id);

        return citaRepository.findById(id).
                orElseThrow(
                        ()-> new RecursoNoEncontradoException("Cita no encontrada con id: "+ id)
                );
    }

    private MedicoResponse obtenerMedicoActivo(Long id){

        log.info("Buscando médico activo con id {} en el servicio remoto", id);
        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id){

        log.info("Buscando médico con id {} en el servicio remoto...", id);
        return medicoClient.obtenerMedicoPorIdSinEstado(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id){

        log.info("Buscando paciente con id {} en el servicio remoto...", id);
        return pacienteClient.obtenerPacientePorIdSinEstado(id);
    }

    private void actualizarDisponibilidadMedico(Long idMedico , Long idDisponibilidad){

        log.info("Actualizando disponibilidad del médico en el servicio remoto...");

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);

        log.info("Disponibilidad del médico actualizada en el servicio remoto");
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico){
        log.info("Validando si el médico esta disponible...");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El médico activo no está disponible para consulta");
    }


    private PacienteResponse obtenerPacienteActivo(Long id){
        log.info("Obteniendo paciente  con id: {}...", id);

        return pacienteClient.obtenerPacienteActivoPorId(id);

    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
            switch (estadoCita) {
                case PENDIENTE, CONFIRMADA ->
                        actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

                case FINALIZADA, CANCELADA ->
                        actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());

                case EN_CURSO ->
                        actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            }
    }
}
