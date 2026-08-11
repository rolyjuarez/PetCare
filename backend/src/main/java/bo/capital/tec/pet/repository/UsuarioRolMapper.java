package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.UsuarioRol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UsuarioRolMapper {
    Long insert(UsuarioRol usuarioRol);
    List<UsuarioRol> selectByUsuarioId(@Param("usuarioId") Long usuarioId);
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
    void deleteByRolId(@Param("rolId") Long rolId);
}
