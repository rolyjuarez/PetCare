package bo.capital.tec.pet.modules.reserva.mapper;

import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VacunaCatalogMapper {
    RegistroVacunacionInfoDTO selectRegistroVacunacion(@Param("id") Long id);
}
