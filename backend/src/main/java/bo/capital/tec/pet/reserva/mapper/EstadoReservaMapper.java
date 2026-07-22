package bo.capital.tec.pet.reserva.mapper;

import bo.capital.tec.pet.reserva.entity.EstadoReserva;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EstadoReservaMapper {
    Long insert(EstadoReserva estadoReserva);
    EstadoReserva selectById(@Param("id") Long id);
    List<EstadoReserva> selectAll();
    EstadoReserva selectByNombre(@Param("nombre") String nombre);
}
