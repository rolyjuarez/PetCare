package bo.capital.tec.pet.modules.proveedor.domain.port;

import bo.capital.tec.pet.modules.proveedor.domain.model.Disponibilidad;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface DisponibilidadRepository {
Long insert(Disponibilidad disponibilidad);
    Disponibilidad selectById(@Param("id") Long id);
    List<Disponibilidad> selectByProveedorYServicio(@Param("proveedorId") Long proveedorId, @Param("servicioId") Long servicioId);
    List<Disponibilidad> selectByServicioId(@Param("servicioId") Long servicioId);
    List<Disponibilidad> selectAll(@Param("proveedorId") Long proveedorId, @Param("servicioId") Long servicioId, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("proveedorId") Long proveedorId, @Param("servicioId") Long servicioId);
    void update(Disponibilidad disponibilidad);
    void softDelete(@Param("id") Long id);
    void deleteByProveedorId(@Param("proveedorId") Long proveedorId);
}
