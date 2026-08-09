package bo.capital.tec.pet.modules.soporte.mapper;

import bo.capital.tec.pet.modules.soporte.entity.Rol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolMapper {

    Rol selectByNombre(@Param("nombre") String nombre);
}
