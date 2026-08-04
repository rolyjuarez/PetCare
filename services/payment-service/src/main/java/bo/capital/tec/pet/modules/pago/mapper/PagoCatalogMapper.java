package bo.capital.tec.pet.modules.pago.mapper;

import bo.capital.tec.pet.modules.pago.dto.PromocionInfoDTO;
import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface PagoCatalogMapper {

    ReservaInfoDTO selectReservaInfo(@Param("reservaId") Long reservaId);

    PromocionInfoDTO selectPromocionActivaByProveedor(@Param("proveedorId") Long proveedorId);

    BigDecimal selectModalidadCostoAdicional(@Param("servicioId") Long servicioId,
                                             @Param("modalidad") String modalidad);
}
