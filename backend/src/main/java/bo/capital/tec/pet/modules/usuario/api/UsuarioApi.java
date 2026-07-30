package bo.capital.tec.pet.modules.usuario.api;

import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.modules.usuario.entity.UsuarioRol;

import java.util.List;

public interface UsuarioApi {
    Usuario findByUsername(String username);
    Usuario selectById(Long id);
    Usuario selectByPersonaId(Long personaId);
    Long insert(Usuario usuario);
    void update(Usuario usuario);
    List<String> findRolesByUsuarioId(Long usuarioId);
    List<String> findPermissionsByUsuarioId(Long usuarioId);
    void updateTokenRefresh(Long id, String tokenRefresh);
    void updateLastAccess(Long id);
    void incrementFailedAttempts(Long id);
    void resetFailedAttempts(Long id);
    void blockUser(Long id);
    Long insertUsuarioRol(UsuarioRol usuarioRol);
}
