package bo.capital.tec.pet.modules.usuario.api.impl;

import bo.capital.tec.pet.modules.usuario.api.UsuarioApi;
import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.modules.usuario.entity.UsuarioRol;
import bo.capital.tec.pet.modules.usuario.mapper.UsuarioMapper;
import bo.capital.tec.pet.modules.usuario.mapper.UsuarioRolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsuarioApiImpl implements UsuarioApi {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRolMapper usuarioRolMapper;

    @Override
    public Usuario findByUsername(String username) {
        return usuarioMapper.findByUsername(username);
    }

    @Override
    public Usuario selectById(Long id) {
        return usuarioMapper.selectById(id);
    }

    @Override
    public Usuario selectByPersonaId(Long personaId) {
        return usuarioMapper.selectByPersonaId(personaId);
    }

    @Override
    public Long insert(Usuario usuario) {
        return usuarioMapper.insert(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        usuarioMapper.update(usuario);
    }

    @Override
    public List<String> findRolesByUsuarioId(Long usuarioId) {
        return usuarioMapper.findRolesByUsuarioId(usuarioId);
    }

    @Override
    public List<String> findPermissionsByUsuarioId(Long usuarioId) {
        return usuarioMapper.findPermissionsByUsuarioId(usuarioId);
    }

    @Override
    public void updateTokenRefresh(Long id, String tokenRefresh) {
        usuarioMapper.updateTokenRefresh(id, tokenRefresh);
    }

    @Override
    public void updateLastAccess(Long id) {
        usuarioMapper.updateLastAccess(id);
    }

    @Override
    public void incrementFailedAttempts(Long id) {
        usuarioMapper.incrementFailedAttempts(id);
    }

    @Override
    public void resetFailedAttempts(Long id) {
        usuarioMapper.resetFailedAttempts(id);
    }

    @Override
    public void blockUser(Long id) {
        usuarioMapper.blockUser(id);
    }

    @Override
    public Long insertUsuarioRol(UsuarioRol usuarioRol) {
        return usuarioRolMapper.insert(usuarioRol);
    }
}
