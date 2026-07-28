package bo.capital.tec.pet.modules.usuario.mapper;

import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UsuarioMapper {
    Long insert(Usuario usuario);
    Usuario selectById(@Param("id") Long id);
    Usuario findByUsername(@Param("username") String username);
    List<String> findRolesByUsuarioId(@Param("usuarioId") Long usuarioId);
    List<String> findPermissionsByUsuarioId(@Param("usuarioId") Long usuarioId);
    List<Usuario> selectAll(@Param("username") String username, @Param("personaNombre") String personaNombre, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("username") String username, @Param("personaNombre") String personaNombre);
    void update(Usuario usuario);
    void softDelete(@Param("id") Long id);
    void updateTokenRefresh(@Param("id") Long id, @Param("tokenRefresh") String tokenRefresh);
    void updateLastAccess(@Param("id") Long id);
    void incrementFailedAttempts(@Param("id") Long id);
    void resetFailedAttempts(@Param("id") Long id);
    void blockUser(@Param("id") Long id);
    Usuario selectByPersonaId(@Param("personaId") Long personaId);
}
