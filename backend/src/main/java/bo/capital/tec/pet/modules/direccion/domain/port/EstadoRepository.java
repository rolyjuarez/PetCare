package bo.capital.tec.pet.modules.direccion.domain.port;

import bo.capital.tec.pet.modules.direccion.domain.model.Estado;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface EstadoRepository {
Long insert(Estado estado);
    Estado selectById(@Param("id") Long id);
    List<Estado> selectByCiudadId(@Param("ciudadId") Long ciudadId);
    List<Estado> selectAll();
}
