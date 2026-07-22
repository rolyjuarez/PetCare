package bo.capital.tec.pet.sucursal.mapper;

import bo.capital.tec.pet.sucursal.entity.Sucursal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SucursalMapper {
    Long insert(Sucursal sucursal);
    Sucursal selectById(@Param("id") Long id);
    List<Sucursal> selectAll(@Param("nombre") String nombre, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre);
    List<Sucursal> selectActive();
    void update(Sucursal sucursal);
    void softDelete(@Param("id") Long id);
}
