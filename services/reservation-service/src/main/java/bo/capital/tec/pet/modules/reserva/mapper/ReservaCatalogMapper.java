package bo.capital.tec.pet.modules.reserva.mapper;

import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservaCatalogMapper {

    ClienteInfoDTO selectCliente(@Param("clienteId") Long clienteId);

    MascotaInfoDTO selectMascota(@Param("mascotaId") Long mascotaId);
}
