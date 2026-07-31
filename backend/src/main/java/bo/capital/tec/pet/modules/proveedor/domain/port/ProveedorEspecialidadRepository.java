package bo.capital.tec.pet.modules.proveedor.domain.port;

import bo.capital.tec.pet.modules.proveedor.domain.model.ProveedorEspecialidad;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProveedorEspecialidadRepository {
Long insert(ProveedorEspecialidad pe);
    List<ProveedorEspecialidad> selectByProveedorId(@Param("proveedorId") Long proveedorId);
    void deleteByProveedorId(@Param("proveedorId") Long proveedorId);
}
