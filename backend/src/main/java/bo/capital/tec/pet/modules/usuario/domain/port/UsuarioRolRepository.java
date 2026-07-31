package bo.capital.tec.pet.modules.usuario.domain.port;

import bo.capital.tec.pet.modules.usuario.domain.model.UsuarioRol;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UsuarioRolRepository {
Long insert(UsuarioRol usuarioRol);
    List<UsuarioRol> selectByUsuarioId(@Param("usuarioId") Long usuarioId);
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
    void deleteByRolId(@Param("rolId") Long rolId);
}
