package bo.capital.tec.pet.modules.pago.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosTarjetaDTO {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    private String numero;

    @NotBlank(message = "El titular es obligatorio")
    private String titular;

    @NotBlank(message = "La fecha de expiración es obligatoria")
    private String expira;

    @NotBlank(message = "El CVV es obligatorio")
    private String cvv;
}
