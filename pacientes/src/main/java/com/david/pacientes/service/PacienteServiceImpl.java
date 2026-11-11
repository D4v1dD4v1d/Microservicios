package com.david.pacientes.service;

import com.david.commons.clients.CitaClient;
import com.david.commons.dto.pacientes.PacienteRequest;
import com.david.commons.dto.pacientes.PacienteResponse;
import com.david.commons.enums.EstadoRegistro;
import com.david.commons.exceptions.ConflictoException;
import com.david.commons.exceptions.RecursoNoEncontradoException;
import com.david.pacientes.entitiy.Paciente;
import com.david.pacientes.mapper.PacienteMapper;
import com.david.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService{
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final CitaClient citaClient;


    @Override
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Obteniendo paciente sin estado...");
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id).
                orElseThrow(
                        ()->new RecursoNoEncontradoException ("No existe un paciente con el id: "+id)
                )
        );
    }

    @Override
    public List<PacienteResponse> lista() {
        log.info("Obteniendo listado de pacientes...");
        return pacienteRepository.findAll().stream()
                .filter(paciente -> paciente.getEstadoRegistro().equals(EstadoRegistro.ACTIVO))
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Obteniendo paciente con id {}.",id);

        return pacienteMapper.entidadAResponse(obtenerPaciente(id));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {

        log.info("Registrando al paciente {}...",request.nombre());

        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);

        pacienteRepository.saveAndFlush(paciente);

        log.info("{} ah sido registrado como paciente éxitosamente.", paciente.obtenerNombreCompleto());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {


        Paciente paciente = obtenerPaciente(id);

        validarPacienteTieneCitasAgendadas(id);

        log.info("Actualizando datos de {}...", paciente.obtenerNombreCompleto());

        validarCambiosUnicos(request,id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion());

        pacienteRepository.saveAndFlush(paciente);

        log.info("Los datos del paciente {} han sido actualizados correctamente.", paciente.obtenerNombreCompleto());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPaciente(id);

        log.info("Eliminando al paciente {}", paciente.obtenerNombreCompleto());

        validarPacienteTieneCitasAgendadas(id);

        paciente.eliminar();

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente eliminado correctamente");

    }

    private Paciente obtenerPaciente(Long id){
        log.info("Obteniendo el paciente...");
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).
                orElseThrow(()->new RecursoNoEncontradoException ("Paciente activo no encontrado con el id: "+id));
    }

    private void validarDatosUnicos(PacienteRequest request){
        log.info("Validando email único {}...",request.email());

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando télefono único {}...",request.telefono());

        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo registrado con el télefono: " + request.telefono());

    }

    private void validarCambiosUnicos(PacienteRequest request, Long id){
        log.info("Validando email único...");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando télefono único...");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo registrado con el télefono: " + request.telefono());

    }

    private void validarPacienteTieneCitasAgendadas(Long idPaciente){

        log.info("Validando si el paciente con id {} tiene citas asignadas en servicio remoto...", idPaciente);

        citaClient.pacienteTieneCitasAsignadas(idPaciente);
    }

    private void validarCitas(Long idCita){

    }
}
