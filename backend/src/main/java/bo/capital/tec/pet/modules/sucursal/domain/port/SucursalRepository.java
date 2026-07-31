package bo.capital.tec.pet.modules.sucursal.domain.port;

import bo.capital.tec.pet.modules.sucursal.domain.model.Sucursal;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SucursalRepository {
Long insert(Sucursal sucursal);
    Sucursal selectById(@Param("id") Long id);
    List<Sucursal> selectAll(@Param("nombre") String nombre, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre);
    List<Sucursal> selectActive();
    void update(Sucursal sucursal);
    void softDelete(@Param("id") Long id);
}
