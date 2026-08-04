package bo.capital.tec.pet.modules.proveedor.mapper;

import bo.capital.tec.pet.modules.proveedor.entity.ProveedorEspecialidad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProveedorEspecialidadMapper {
    Long insert(ProveedorEspecialidad pe);
    List<ProveedorEspecialidad> selectByProveedorId(@Param("proveedorId") Long proveedorId);
    ProveedorEspecialidad selectByProveedorYServicio(@Param("proveedorId") Long proveedorId, @Param("servicioId") Long servicioId);
    void updateRequiereCertificado(@Param("proveedorId") Long proveedorId, @Param("servicioId") Long servicioId, @Param("requiere") Boolean requiere);
    void deleteByProveedorId(@Param("proveedorId") Long proveedorId);
}
