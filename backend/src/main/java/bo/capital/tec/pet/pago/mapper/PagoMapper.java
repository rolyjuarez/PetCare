package bo.capital.tec.pet.pago.mapper;

import bo.capital.tec.pet.pago.entity.Pago;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PagoMapper {
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
