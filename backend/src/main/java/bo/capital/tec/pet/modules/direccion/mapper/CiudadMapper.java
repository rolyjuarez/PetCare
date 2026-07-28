package bo.capital.tec.pet.modules.direccion.mapper;

import bo.capital.tec.pet.modules.direccion.entity.Ciudad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CiudadMapper {
    Long insert(Ciudad ciudad);
    Ciudad selectById(@Param("id") Long id);
    List<Ciudad> selectAll();
    Ciudad selectByCodigo(@Param("codigo") String codigo);
}
