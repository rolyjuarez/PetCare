package bo.capital.tec.pet.modules.pago.mapper;

import bo.capital.tec.pet.modules.pago.entity.Pago;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PagoMapper {

    void insert(Pago pago);

    Pago selectById(@Param("id") Long id);

    Pago selectByReservaId(@Param("reservaId") Long reservaId);

    List<Pago> selectByReservaIds(@Param("reservaId") Long reservaId,
                                  @Param("limit") int limit, @Param("offset") int offset);

    long countByReservaId(@Param("reservaId") Long reservaId);

    void updateEstado(@Param("id") Long id, @Param("estadoPago") String estadoPago,
                      @Param("estadoSync") String estadoSync,
                      @Param("referenciaTransaccion") String referenciaTransaccion,
                      @Param("intencionId") String intencionId,
                      @Param("fechaPago") java.time.LocalDateTime fechaPago,
                      @Param("metodoPago") String metodoPago);

    void updateDescuentos(@Param("id") Long id, @Param("monto") java.math.BigDecimal monto,
                          @Param("montoOriginal") java.math.BigDecimal montoOriginal,
                          @Param("descuentoTotal") java.math.BigDecimal descuentoTotal);
}
