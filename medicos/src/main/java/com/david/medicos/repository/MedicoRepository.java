package com.david.medicos.repository;

import com.david.commons.enums.EstadoRegistro;
import com.david.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico,Long> {
     List<Medico> findByEstadoRegistro(EstadoRegistro estadoRegistro);

     Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

     boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);

     boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);

     boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(String cedula, EstadoRegistro estadoRegistro);

     boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(String email, EstadoRegistro estadoRegistro, long id);

     boolean existsByTelefonoAndEstadoRegistroAndIdNot(String telefono, EstadoRegistro estadoRegistro, long id);

     boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(String cedula, EstadoRegistro estadoRegistro, long  id);


}
