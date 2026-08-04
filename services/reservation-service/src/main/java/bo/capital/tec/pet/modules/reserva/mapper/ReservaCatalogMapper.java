package bo.capital.tec.pet.modules.reserva.mapper;

import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservaCatalogMapper {

    ClienteInfoDTO selectCliente(@Param("clienteId") Long clienteId);

    ProveedorInfoDTO selectProveedor(@Param("proveedorId") Long proveedorId);

    ServicioInfoDTO selectServicio(@Param("servicioId") Long servicioId);

    MascotaInfoDTO selectMascota(@Param("mascotaId") Long mascotaId);

    List<ModalidadInfoDTO> selectModalidadesByServicio(@Param("servicioId") Long servicioId);

    boolean selectModalidadValida(@Param("servicioId") Long servicioId, @Param("modalidad") String modalidad);
}
