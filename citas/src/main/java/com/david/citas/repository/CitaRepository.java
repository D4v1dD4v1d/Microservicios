package com.david.citas.repository;

import com.david.citas.entity.Cita;
import com.david.citas.enums.EstadoCita;
import com.david.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    /*
    * SELECT COUNT(*) FROM CITAS WHERE ID_MEDICO = ? AND
    *           ESTADO_REGISTRO = ? AND ESTADO_CITA IN(?)
    * */
    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas);

    boolean existsByIdMedicoAndIdNot(Long idMedico, Long idCita);
}
