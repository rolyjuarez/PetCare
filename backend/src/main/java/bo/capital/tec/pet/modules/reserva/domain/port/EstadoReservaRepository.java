package bo.capital.tec.pet.modules.reserva.domain.port;

import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface EstadoReservaRepository {
Long insert(EstadoReserva estadoReserva);
    EstadoReserva selectById(@Param("id") Long id);
    List<EstadoReserva> selectAll();
    EstadoReserva selectByNombre(@Param("nombre") String nombre);
}
