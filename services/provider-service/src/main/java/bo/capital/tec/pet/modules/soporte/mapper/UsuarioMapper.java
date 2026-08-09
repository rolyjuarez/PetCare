package bo.capital.tec.pet.modules.soporte.mapper;

import bo.capital.tec.pet.modules.soporte.entity.Usuario;
import bo.capital.tec.pet.modules.soporte.entity.UsuarioRol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UsuarioMapper {

    Usuario findByUsername(@Param("username") String username);

    Long insert(Usuario usuario);

    Long insertUsuarioRol(UsuarioRol usuarioRol);
}
