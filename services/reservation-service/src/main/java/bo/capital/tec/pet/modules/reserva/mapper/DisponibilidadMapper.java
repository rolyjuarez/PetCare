package bo.capital.tec.pet.modules.reserva.mapper;

import bo.capital.tec.pet.modules.reserva.entity.Disponibilidad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DisponibilidadMapper {

    List<Disponibilidad> selectByProveedorServicio(@Param("proveedorId") Long proveedorId,
                                                   @Param("servicioId") Long servicioId);
}
