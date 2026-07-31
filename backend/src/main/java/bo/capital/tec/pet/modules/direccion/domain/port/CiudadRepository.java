package bo.capital.tec.pet.modules.direccion.domain.port;

import bo.capital.tec.pet.modules.direccion.domain.model.Ciudad;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CiudadRepository {
Long insert(Ciudad ciudad);
    Ciudad selectById(@Param("id") Long id);
    List<Ciudad> selectAll();
    Ciudad selectByCodigo(@Param("codigo") String codigo);
}
