package bo.capital.tec.pet.modules.pago.domain.port;

import bo.capital.tec.pet.modules.pago.domain.model.Pago;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PagoRepository {
Long insert(Pago pago);
    Pago selectById(@Param("id") Long id);
    List<Pago> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    Pago selectByReservaId(@Param("reservaId") Long reservaId);
    List<Pago> selectByEstadoPago(@Param("estadoPago") String estadoPago, @Param("offset") int offset, @Param("limit") int limit);
    void update(Pago pago);
    void updateEstadoPago(@Param("id") Long id, @Param("estadoPago") String estadoPago);
    void softDelete(@Param("id") Long id);
}
