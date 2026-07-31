package bo.capital.tec.pet.modules.direccion.domain.port;

import bo.capital.tec.pet.modules.direccion.domain.model.Direccion;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface DireccionRepository {
Long insert(Direccion direccion);
    Direccion selectById(@Param("id") Long id);
    List<Direccion> selectByCiudadId(@Param("ciudadId") Long ciudadId);
    void update(Direccion direccion);
    void softDelete(@Param("id") Long id);
}
