package com.david.medicos.service;

import com.david.commons.clients.CitaClient;
import com.david.commons.dto.medicos.MedicoRequest;
import com.david.commons.dto.medicos.MedicoResponse;
import com.david.commons.enums.DisponibilidadMedico;
import com.david.commons.enums.EspecialidadMedico;
import com.david.commons.enums.EstadoRegistro;
import com.david.commons.exceptions.ConflictoException;
import com.david.commons.exceptions.RecursoNoEncontradoException;
import com.david.medicos.entity.Medico;
import com.david.medicos.mapper.MedicoMapper;
import com.david.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService{
    private final MedicoRepository medicoRepository;
    private final CitaClient citaClient;

    private final MedicoMapper medicoMapper;

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("obtener medico sin estado");

        return medicoMapper.entidadAResponse( medicoRepository.findById(id).
                orElseThrow(
                        ()-> new RecursoNoEncontradoException("Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del médico {}", medico.obtenerNombreCompleto());

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del médico {} cambió de {} a {}"
        ,medico.obtenerNombreCompleto(), disponibilidadAnterior,nuevaDisponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> lista() {
        log.info("Listando todos lo médicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        log.info("Obteniendo médico");

        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo médico...");

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(
                        request.idEspecialidad()
                )
        );

        medicoRepository.saveAndFlush(medico);

        log.info("Nuevo médico registrado: {}", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico =obtenerMedicoActivoPorId(id);

        validarMedicoTieneCitasAgendadas(id);

        validarCambiosUnicos(request,id);

        log.info("Actualizando médico con id: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Médico {} actualizado correctamente.", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);

        log.info("Eliminando al médico {}", medico.obtenerNombreCompleto());

        validarMedicoTieneCitasAgendadas(id);

        medico.eliminar();

        medicoRepository.saveAndFlush(medico);

        log.info("Médico {} eliminado exitósamente", medico.obtenerNombreCompleto());

    }

    private Medico obtenerMedicoActivoPorId(Long id){
        log.info("Buscando médico activo con id {}", id);
        return medicoRepository.findByIdAndEstadoRegistro(id,EstadoRegistro.ACTIVO)
                .orElseThrow(()->new RecursoNoEncontradoException("Médico activo no encontrado con el id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando télefono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un médico activo registrado con el télefono: " + request.telefono());

        log.info("Validando cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un médico activo registrado con la cédula: " + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){
        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando télefono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un médico activo registrado con el télefono: " + request.telefono());

        log.info("Validando cédula profesional única...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un médico activo registrado con la cédula: " + request.cedulaProfesional());
    }
    private void validarMedicoTieneCitasAgendadas(Long idMedico){

        log.info("Validando si el médico con id {} tiene citas agregadas en servicio remoto...", idMedico);

        citaClient.medicoTieneCitasAsignadas(idMedico);
    }
}
