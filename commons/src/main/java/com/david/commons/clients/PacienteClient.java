package com.david.commons.clients;


import com.david.commons.dto.pacientes.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacienteClient {

    @GetMapping("/id-paciente/{id}")
    PacienteResponse obtenerPacientePorIdSinEstado(@PathVariable Long id);

    @GetMapping("/{id}")
    PacienteResponse obtenerPacienteActivoPorId(@PathVariable Long id);

}
