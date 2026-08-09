package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.pago.dto.PromocionInfoDTO;

import java.math.BigDecimal;

public interface PagoProviderClient {

    PromocionInfoDTO getPromocionActiva(Long proveedorId, Long servicioId);

    void incrementarUsosPromocion(Long promocionId);

    void decrementarUsosPromocion(Long promocionId);

    BigDecimal getCostoAdicionalModalidad(Long servicioId, String modalidad);
}
