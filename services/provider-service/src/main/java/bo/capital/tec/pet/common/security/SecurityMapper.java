package bo.capital.tec.pet.common.security;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SecurityMapper {

    UsuarioAuth selectUsuarioByUsername(@Param("username") String username);

    List<String> selectRolesByUsuarioId(@Param("usuarioId") Long usuarioId);
}
