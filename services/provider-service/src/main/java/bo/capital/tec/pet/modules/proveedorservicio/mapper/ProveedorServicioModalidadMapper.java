package bo.capital.tec.pet.modules.proveedorservicio.mapper;

import bo.capital.tec.pet.modules.proveedorservicio.entity.ProveedorServicioModalidad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProveedorServicioModalidadMapper {
    Long insert(ProveedorServicioModalidad modalidad);
    List<ProveedorServicioModalidad> selectByProveedorServicioId(@Param("proveedorServicioId") Long proveedorServicioId);
    void deleteByProveedorServicioId(@Param("proveedorServicioId") Long proveedorServicioId);
}
