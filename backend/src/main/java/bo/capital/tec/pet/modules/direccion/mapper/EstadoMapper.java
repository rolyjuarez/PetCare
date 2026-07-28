package bo.capital.tec.pet.modules.direccion.mapper;

import bo.capital.tec.pet.modules.direccion.entity.Estado;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EstadoMapper {
    Long insert(Estado estado);
    Estado selectById(@Param("id") Long id);
    List<Estado> selectByCiudadId(@Param("ciudadId") Long ciudadId);
    List<Estado> selectAll();
}
