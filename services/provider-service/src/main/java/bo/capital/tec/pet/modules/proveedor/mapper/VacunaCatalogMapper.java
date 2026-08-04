package bo.capital.tec.pet.modules.proveedor.mapper;

import bo.capital.tec.pet.modules.proveedor.dto.VacunaInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VacunaCatalogMapper {

    VacunaInfoDTO selectRegistroVacunacion(@Param("id") Long id);
}
