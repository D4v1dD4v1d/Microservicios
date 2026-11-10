package com.david.auth.mapper;

import com.david.auth.dto.UsuarioRequest;
import com.david.auth.dto.UsuarioResponse;
import com.david.auth.entities.Rol;
import com.david.auth.entities.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
@Component
public class UsuarioMapper {

    public UsuarioResponse entityToResponse(Usuario usuario){
        return usuario == null
                ? null
                : new UsuarioResponse(
                usuario.getUsername(),
                usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet()));
    }

    public Usuario requestToEntity(UsuarioRequest request, String password, Set<Rol> roles) {
        if (request == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username().trim());
        usuario.setPassword(password.trim());
        usuario.setRoles(roles);
        return usuario;
    }
}
