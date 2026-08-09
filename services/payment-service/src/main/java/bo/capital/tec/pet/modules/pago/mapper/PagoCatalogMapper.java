package bo.capital.tec.pet.modules.pago.mapper;

import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PagoCatalogMapper {

    ReservaInfoDTO selectReservaInfo(@Param("reservaId") Long reservaId);
}
