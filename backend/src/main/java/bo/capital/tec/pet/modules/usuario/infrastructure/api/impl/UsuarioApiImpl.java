package bo.capital.tec.pet.modules.usuario.infrastructure.api.impl;

import bo.capital.tec.pet.modules.usuario.api.UsuarioApi;
import bo.capital.tec.pet.modules.usuario.domain.model.Usuario;
import bo.capital.tec.pet.modules.usuario.domain.model.UsuarioRol;
import bo.capital.tec.pet.modules.usuario.domain.port.UsuarioRepository;
import bo.capital.tec.pet.modules.usuario.domain.port.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsuarioApiImpl implements UsuarioApi {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario selectById(Long id) {
        return usuarioRepository.selectById(id);
    }

    @Override
    public Usuario selectByPersonaId(Long personaId) {
        return usuarioRepository.selectByPersonaId(personaId);
    }

    @Override
    public Long insert(Usuario usuario) {
        return usuarioRepository.insert(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.update(usuario);
    }

    @Override
    public List<String> findRolesByUsuarioId(Long usuarioId) {
        return usuarioRepository.findRolesByUsuarioId(usuarioId);
    }

    @Override
    public List<String> findPermissionsByUsuarioId(Long usuarioId) {
        return usuarioRepository.findPermissionsByUsuarioId(usuarioId);
    }

    @Override
    public void updateTokenRefresh(Long id, String tokenRefresh) {
        usuarioRepository.updateTokenRefresh(id, tokenRefresh);
    }

    @Override
    public void updateLastAccess(Long id) {
        usuarioRepository.updateLastAccess(id);
    }

    @Override
    public void incrementFailedAttempts(Long id) {
        usuarioRepository.incrementFailedAttempts(id);
    }

    @Override
    public void resetFailedAttempts(Long id) {
        usuarioRepository.resetFailedAttempts(id);
    }

    @Override
    public void blockUser(Long id) {
        usuarioRepository.blockUser(id);
    }

    @Override
    public Long insertUsuarioRol(UsuarioRol usuarioRol) {
        return usuarioRolRepository.insert(usuarioRol);
    }
}
