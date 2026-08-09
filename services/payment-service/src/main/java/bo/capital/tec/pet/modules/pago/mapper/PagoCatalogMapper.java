package bo.capital.tec.pet.modules.pago.mapper;

import bo.capital.tec.pet.modules.pago.dto.PromocionInfoDTO;
import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface PagoCatalogMapper {

    ReservaInfoDTO selectReservaInfo(@Param("reservaId") Long reservaId);

    PromocionInfoDTO selectPromocionActivaByProveedorYServicio(@Param("proveedorId") Long proveedorId,
                                                               @Param("servicioId") Long servicioId);

    void incrementarUsosPromocion(@Param("promocionId") Long promocionId);

    void decrementarUsosPromocion(@Param("promocionId") Long promocionId);

    BigDecimal selectModalidadCostoAdicional(@Param("servicioId") Long servicioId,
                                             @Param("modalidad") String modalidad);
}
