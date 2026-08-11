package bo.capital.tec.pet.modules.proveedor.mapper;

import bo.capital.tec.pet.modules.proveedor.dto.ProveedorCatalogoDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorEspecialidadInfoDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProveedorCatalogMapper {

    ProveedorInfoDTO selectProveedorByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Long> selectProveedoresByServicioId(@Param("servicioId") Long servicioId);

    List<ProveedorCatalogoDTO> selectCatalogo(@Param("offset") int offset, @Param("limit") int limit);

    long countCatalogo();

    List<ProveedorEspecialidadInfoDTO> selectEspecialidades(@Param("proveedorId") Long proveedorId);
}
