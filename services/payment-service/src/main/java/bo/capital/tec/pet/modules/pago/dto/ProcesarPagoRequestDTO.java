package bo.capital.tec.pet.modules.pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarPagoRequestDTO {

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    private String modalidadPago;

    private DatosTarjetaDTO tarjeta;
}
