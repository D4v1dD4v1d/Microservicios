package com.david.auth.services;

import com.david.auth.dto.UsuarioRequest;
import com.david.auth.dto.UsuarioResponse;
import com.david.auth.dto.UsuarioUpdateRequest;

import java.util.Set;

public interface UsuarioService {
    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse actualizar(String username, UsuarioUpdateRequest request);

    void eliminar(String username);
}
