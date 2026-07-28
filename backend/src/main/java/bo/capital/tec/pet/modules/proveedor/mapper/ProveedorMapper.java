package bo.capital.tec.pet.modules.proveedor.mapper;

import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProveedorMapper {
    Long insert(Proveedor proveedor);
    Proveedor selectById(@Param("id") Long id);
    List<Proveedor> selectAll(@Param("nombre") String nombre, @Param("verificado") Boolean verificado, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("verificado") Boolean verificado);
    List<Proveedor> findByServicioId(@Param("servicioId") Long servicioId);
    List<Proveedor> searchNearby(@Param("latitud") BigDecimal latitud, @Param("longitud") BigDecimal longitud, @Param("radioKm") BigDecimal radioKm);
    Proveedor findByUsuarioId(@Param("usuarioId") Long usuarioId);
    void update(Proveedor proveedor);
    void softDelete(@Param("id") Long id);
}
